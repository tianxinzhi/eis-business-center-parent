package com.prolog.eis.bc.service.outboundtask.impl;

import com.prolog.eis.bc.dao.OutboundTaskBindDetailMapper;
import com.prolog.eis.bc.dao.OutboundTaskBindMapper;
import com.prolog.eis.bc.facade.dto.ContainerSelectorDto;
import com.prolog.eis.bc.facade.vo.OutboundStrategyConfigVo;
import com.prolog.eis.bc.feign.container.EisContainerRouteClient;
import com.prolog.eis.bc.feign.container.EisContainerStoreFeign;
import com.prolog.eis.bc.service.outboundtask.ContainerOutDispatchService;
import com.prolog.eis.common.util.FileLogHelper;
import com.prolog.eis.common.util.PrologDateUtils;
import com.prolog.eis.component.algorithm.composeorder.configuration.ComposeOrderContants;
import com.prolog.eis.component.algorithm.composeorder.entity.*;
import com.prolog.eis.core.model.base.container.ContainerStore;
import com.prolog.eis.core.model.base.locator.WhLocator;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskBind;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskBindDetail;
import com.prolog.eis.core.model.biz.outbound.PickingOrder;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategySourceAreaConfig;
import com.prolog.eis.router.vo.ContainerLocationVo;
import com.prolog.framework.common.message.RestMessage;
import com.prolog.framework.utils.MapUtils;
import com.prolog.upcloud.base.inventory.dto.EisSelectorInv;
import com.prolog.upcloud.base.inventory.vo.EisInvContainerStoreSubVo;
import com.prolog.upcloud.base.inventory.vo.EisInvContainerStoreVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/9/13
 **/
@Service
public class ContainerOutDispatchServiceImpl implements ContainerOutDispatchService {

    private static final Logger logger = LoggerFactory.getLogger(ContainerOutDispatchServiceImpl.class);

    @Autowired
    private OutboundTaskBindMapper outboundTaskBindMapper;
    @Autowired
    private OutboundTaskBindDetailMapper outboundTaskBindDetailMapper;
    @Autowired
    private EisContainerStoreFeign containerStoreFeign;
    @Autowired
    private EisContainerRouteClient eisContainerRouteClient;

    @Override
    public boolean outContainerForPickingOrder(@NotNull WarehouseDto warehouseDto, OutboundStrategyConfigVo config) {
        List<StationDto> stationDtos = warehouseDto.getStationList().stream().filter(p -> p.getIsLock() == ComposeOrderContants.STATUS_UNLOCK && p.isContainerLimitMax() && p.getNeedPickingOrder() != null).collect(Collectors.toList());

        while (true) {
            StationDto station = this.getBestStation(stationDtos);
            if (station == null) {
                break;
            }
            boolean isAllChuKu = this.outContainer(station, warehouseDto, config);

            // ??????????????????????????????????????????,???????????????????????????
            if (isAllChuKu) {
                stationDtos.remove(station);
            } else {
                // ??????????????????????????????????????????????????????????????????????????????????????????
                if (station.getChuKuLxCount() >= station.getMaxLxCacheCount()) {
                    logger.info(String.format("??????[%s],???????????????[%s],???????????????[%s]", station.getStationId(), station.getChuKuLxCount(), station.getMaxLxCacheCount()));
                    stationDtos.remove(station);
                }
            }

        }
        return false;
    }


    /**
     * ???????????????????????????
     *
     * @param station
     * @param outItemId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public boolean outContainerForItemId(StationDto station, String outItemId, OutboundStrategyConfigVo config) throws Exception {
        /**
         * 1.??????????????????????????????
         * 2.????????????????????????????????????????????????????????????????????????????????????????????????????????????
         * 3.??????????????????????????????????????????????????????????????????
         *
         */
        int storeMatchingStrategy = config.getStoreMatchingStrategy();
        EisSelectorInv eisSelectorInv = new EisSelectorInv();
        if (storeMatchingStrategy == 1) {
            //???????????????
            eisSelectorInv.setItemId(outItemId);
        } else {
            //???????????????
            eisSelectorInv.setLotId(outItemId);
        }
        RestMessage<List<EisInvContainerStoreVo>> invRestMessage = containerStoreFeign.findByItemOrLotId(eisSelectorInv);
        if (!invRestMessage.isSuccess() || invRestMessage.getData() == null) {
            logger.error("????????????????????????{} or ??????????????????", invRestMessage.getMessage());
            return false;
        }
        List<EisInvContainerStoreVo> containerStoreAll = invRestMessage.getData();
        List<EisInvContainerStoreVo> containerStore = containerStoreAll.stream().filter(p -> p.getTaskType() == 0).collect(Collectors.toList());
        List<String> containerNoList = containerStore.stream().map(p -> p.getContainerNo()).collect(Collectors.toList());
        RestMessage<List<ContainerLocationVo>> routeRestMessage = eisContainerRouteClient.findLocationByContainerList(containerNoList);
        if (!routeRestMessage.isSuccess() || routeRestMessage.getData() == null) {
            logger.error("????????????????????????{} or ??????????????????", routeRestMessage.getMessage());
            return false;
        }
        ContainerSelectorDto containerSelectorDto = compareContainer(containerStore, routeRestMessage.getData(), config);
        this.bindStation(containerSelectorDto.getStoreId(), containerSelectorDto.getContainerNo(),
                containerSelectorDto.getEisInvContainerStoreSubVoList().get(0).getContainerStoreSubNo(), station, outItemId, containerSelectorDto.getBindNum(), containerSelectorDto.getEisInvContainerStoreSubVoList().get(0).getLotId());
        return false;
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param containerStoreList
     * @param containerLocationVoList
     * @param config
     * @return
     */
    private ContainerSelectorDto compareContainer(List<EisInvContainerStoreVo> containerStoreList, List<ContainerLocationVo> containerLocationVoList, OutboundStrategyConfigVo config) {
        ContainerSelectorDto res = null;
        //??????????????????????????????????????????
        List<ContainerSelectorDto> containerSelectorDtoList = new ArrayList<>();
        List<ContainerLocationVo> containerLocationVos = containerLocationVoList.stream().filter(p -> p.getEquipmentLock() == 0 && p.getIsLock() == 0 && p.getAscentLockState() == 0 && p.getLocationType() == 1).collect(Collectors.toList());
        Iterator<EisInvContainerStoreVo> iterator1 = containerStoreList.iterator();
        Iterator<ContainerLocationVo> iterator2 = containerLocationVos.iterator();
        while (iterator1.hasNext()) {
            while (iterator2.hasNext()) {
                EisInvContainerStoreVo eisInvContainerStoreVo = iterator1.next();
                ContainerLocationVo containerLocationVo = iterator2.next();
                if (eisInvContainerStoreVo.getContainerNo().equals(containerLocationVo.getContainerNo())) {
                    /**
                     * ????????????
                     * 1.???????????? * ???????????????  <= ??????-????????????  ????????????
                     * 2.???????????? * ???????????????  > ??????-???????????? ????????????
                     *      2.1??????????????????????????????????????????????????????
                     *      2.2??????????????????????????????????????????
                     */
                    boolean b = checkProhibitExpiryDate(config.getProhibitExpiryDateRate(), eisInvContainerStoreVo);
                    if (!b) {
                        continue;
                    }
                    ContainerSelectorDto containerSelectorDto = new ContainerSelectorDto();
                    containerSelectorDto.setAreaNo(containerLocationVo.getCurrentArea());
                    containerSelectorDto.setContainerNo(eisInvContainerStoreVo.getContainerNo());
                    containerSelectorDto.setX(containerLocationVo.getX());
                    containerSelectorDto.setY(containerLocationVo.getY());
                    containerSelectorDto.setStoreId(eisInvContainerStoreVo.getId());
                    containerSelectorDto.setDeptNum(containerLocationVo.getDeptNum());
                    containerSelectorDto.setEisInvContainerStoreSubVoList(eisInvContainerStoreVo.getContainerStoreSubList());
                    containerSelectorDtoList.add(containerSelectorDto);
                    iterator1.remove();
                    iterator2.remove();
                }
            }
        }
        containerSelectorDtoList = findExpiryDate(config.getOutboundExpiryDateRate(), containerSelectorDtoList);
        List<OutboundStrategySourceAreaConfig> outboundStrategySourceAreaConfigList = config.getOutboundStrategySourceAreaConfigList();
        outboundStrategySourceAreaConfigList.sort(Comparator.comparing(OutboundStrategySourceAreaConfig::getPriority));
        for (OutboundStrategySourceAreaConfig outboundStrategySourceAreaConfig : outboundStrategySourceAreaConfigList) {
            String areaNo = outboundStrategySourceAreaConfig.getAreaNo();
            int clearStoreStrategy = config.getClearStoreStrategy();
            int outOriginX = outboundStrategySourceAreaConfig.getX();
            int outOriginY = outboundStrategySourceAreaConfig.getY();
            List<ContainerSelectorDto> newContainerSelector = containerSelectorDtoList.stream().filter(p -> p.getAreaNo().equals(areaNo)).collect(Collectors.toList());
            for (ContainerSelectorDto containerSelectorDto : newContainerSelector) {
                containerSelectorDto.setDistance(Math.abs(containerSelectorDto.getX() - outOriginX) + Math.abs(containerSelectorDto.getY() - outOriginY));
            }
            compareContainer(newContainerSelector, clearStoreStrategy);
            if (!newContainerSelector.isEmpty()) {
                res = newContainerSelector.get(0);
                break;
            }

        }
        return res;
    }

    /**
     * ??????
     *
     * @param newContainerSelector
     * @param clearStoreStrategy
     * @return
     */
    private void compareContainer(List<ContainerSelectorDto> newContainerSelector, int clearStoreStrategy) {
        newContainerSelector.sort(new Comparator<ContainerSelectorDto>() {
            @Override
            public int compare(ContainerSelectorDto o1, ContainerSelectorDto o2) {
                int a = o1.getDeptNum() - o2.getDeptNum();
                if (a == 0) {
                    int b = 0;
                    if (clearStoreStrategy == 1) {
                        if (o1.getEisInvContainerStoreSubVoList().get(0).getQty() - o1.getEisInvContainerStoreSubVoList().get(0).getQty() > 0) {
                            b = 1;
                        } else {
                            b = -1;
                        }
                    }
                    if (b == 0) {
                        return o1.getDistance() - o2.getDistance();
                    }
                    return b;
                }
                return a;
            }
        });
    }

    /**
     * ??????????????????????????????
     *
     * @param rate
     * @param eisInvContainerStoreVo
     * @return
     */
    private boolean checkProhibitExpiryDate(int rate, EisInvContainerStoreVo eisInvContainerStoreVo) {
        List<EisInvContainerStoreSubVo> containerStoreSubList = eisInvContainerStoreVo.getContainerStoreSubList();
        for (EisInvContainerStoreSubVo containerStoreSub : containerStoreSubList) {
            Date batchExpiredDate = containerStoreSub.getBatchExpiredDate();
            Long shelfLifeDays = containerStoreSub.getShelfLifeDays();
            long currentTimeMillis = System.currentTimeMillis();
            long remainDay = (batchExpiredDate.getTime() - currentTimeMillis) / (1000 * 60 * 60 * 24);
            if (shelfLifeDays * (rate / 100) > remainDay) {
                return false;
            }
        }
        return true;
    }

    private List<ContainerSelectorDto> findExpiryDate(int rate, List<ContainerSelectorDto> containerSelectorDtoList) {
        List<ContainerSelectorDto> newContainerSelectorDtoList = new ArrayList<>();
        for (ContainerSelectorDto containerSelectorDto : containerSelectorDtoList) {
            List<EisInvContainerStoreSubVo> eisInvContainerStoreSubVoList = containerSelectorDto.getEisInvContainerStoreSubVoList();
            for (EisInvContainerStoreSubVo containerStoreSub : eisInvContainerStoreSubVoList) {
                Date batchExpiredDate = containerStoreSub.getBatchExpiredDate();
                Long shelfLifeDays = containerStoreSub.getShelfLifeDays();
                long currentTimeMillis = System.currentTimeMillis();
                long remainDay = (batchExpiredDate.getTime() - currentTimeMillis) / (1000 * 60 * 60 * 24);
                if (shelfLifeDays * (rate / 100) > remainDay) {
                    newContainerSelectorDtoList.add(containerSelectorDto);
                }
            }
        }
        if (!newContainerSelectorDtoList.isEmpty()) {
            containerSelectorDtoList = newContainerSelectorDtoList;
        }
        return containerSelectorDtoList;
    }

    /**
     * ????????????
     *
     * @param station
     * @param warehouseDto
     * @return
     */
    private boolean outContainer(StationDto station, WarehouseDto warehouseDto, OutboundStrategyConfigVo config) {
        PickingOrderDto needPickingOrder = station.getNeedPickingOrder();
        String outItemId = needPickingOrder.getOutItemId(warehouseDto.getChuKuFailItemIdHs());
        if (StringUtils.isBlank(outItemId)) {
            return true;
        }
        boolean isOutSuccess = false;
        /**
         * ??????????????????
         */
        try {
            isOutSuccess = this.outContainerForItemId(station, outItemId, config);
        } catch (Exception ex) {
            isOutSuccess = false;
            logger.error("??????????????????  {}", ex.toString());
        }
        if (isOutSuccess) {
            station.setChuKuLxCount(station.getChuKuLxCount() + 1);
            // ??????????????????????????????????????????????????????
            if (station.isContainerLimitMax()) {
                return true;
            }

            return station.checkIsAllBinding();
        } else {
            logger.info("++++++++++++++++++{}??????????????????,?????????????????????++++++++++++++++++", outItemId);
            // ??????????????????????????????????????????????????????????????????Map???
            warehouseDto.getChuKuFailItemIdHs().add(outItemId);
            logger.info(warehouseDto.getChuKuFailItemIdHs().toString());
            return false;
        }
    }

    /**
     * ????????????
     *
     * @param storeId
     * @param containerNo
     * @param station
     * @param itemId
     * @param bindNum
     * @throws Exception
     */
    private void bindStation(String storeId, String containerNo, String containerSubNo, StationDto station, String itemId, float bindNum, String lotId) throws Exception {
        if (station == null) {
            throw new Exception("????????????!");
        }

        if (station.getNeedPickingOrder() == null) {
            throw new Exception("??????" + station.getStationId() + "????????????????????????!");
        }
        try {
            ContainerDto container = new ContainerDto();
            container.setContainerNo(containerNo);
            container.setStationId(station.getStationId());
            ContainerSubDto containerSubDto = new ContainerSubDto();
            containerSubDto.setItemId(itemId);
            containerSubDto.setContainerNo(containerNo);
            containerSubDto.setContainerSubNo(containerSubNo);
            if (bindNum == 0.0) {
                throw new Exception(String.format("??????[%s]?????????????????????", containerNo));
            } else {
                containerSubDto.setItemNum(bindNum);
            }
            container.getContainerSubList().add(containerSubDto);


            // ???????????????????????????????????????????????????????????????????????????????????????
            boolean isLxBinding = false;
            PickingOrderDto pickingOrder = station.getNeedPickingOrder();
            for (int i = 0; i < pickingOrder.getOutboundTaskList().size(); i++) {
                BizOutTask outTask = pickingOrder.getOutboundTaskList().get(i);
                boolean isOutTaskBinding = this.bindOutTask(pickingOrder, container, containerSubDto, outTask, station, itemId, lotId);
                if (isOutTaskBinding) {
                    isLxBinding = true;
                }
            }

            if (!isLxBinding) {
                throw new Exception("??????????????????????????????");
            }
        } catch (Exception ex) {
            throw new Exception(ex.toString());
        }
    }

    /**
     * ??????????????????
     *
     * @param pickingOrder
     * @param container
     * @param outTask
     * @param station
     * @param itemId
     * @return
     */
    private boolean bindOutTask(PickingOrderDto pickingOrder, ContainerDto container, ContainerSubDto containerSubDto, BizOutTask outTask, StationDto station, String itemId, String lotId) throws Exception {
        BizOutTaskDetail outTaskDetail = null;
        for (BizOutTaskDetail detail : outTask.getBizOutTaskDetailList()) {
            if (detail.getItemId().equals(itemId)) {
                if (!detail.checkBindingFinish()) {
                    outTaskDetail = detail;
                    break;
                }
            }
        }
        if (outTaskDetail == null) {
            return false;
        }
        float remainderBindingCount = outTaskDetail.getRemainderBindingCount();
        if (remainderBindingCount <= 0) {
            throw new Exception("?????????????????????");
        }
        return this.bindOutaskDetail(pickingOrder, outTaskDetail, container, containerSubDto, station, itemId, lotId);
    }

    /**
     * ????????????????????????
     *
     * @param pickingOrder
     * @param outTaskDetail
     * @param container
     * @param containerSubDto
     * @param station
     * @param itemId
     * @param lotId
     * @return
     * @throws Exception
     */
    private boolean bindOutaskDetail(PickingOrderDto pickingOrder, BizOutTaskDetail outTaskDetail, ContainerDto container, ContainerSubDto containerSubDto, StationDto station, String itemId, String lotId) throws Exception {
        float detailRemainderBindingNum = outTaskDetail.getRemainderBindingCount();
        float containerRemainderBindingNum = containerSubDto.getRemainderBindingNum();
        if (detailRemainderBindingNum < 0) {
            throw new Exception(String.format("detailRemainderBindingNum??????0  ??????{%s}", detailRemainderBindingNum));
        }
        if (containerRemainderBindingNum < 0) {
            throw new Exception(String.format("containerRemainderBindingNum??????0  ??????{%s}", containerRemainderBindingNum));
        }
        if (detailRemainderBindingNum == 0) {
            return false;
        }
        if (containerRemainderBindingNum == 0) {
            return false;
        }
        float pickNum = detailRemainderBindingNum;
        if (pickNum > containerRemainderBindingNum) {
            pickNum = containerRemainderBindingNum;
        }
        outTaskDetail.addBindingNum(pickNum);
        containerSubDto.getContainerAndOutDetailBindingMap().put(outTaskDetail.getId(), pickNum);
        if (!pickingOrder.getContainerList().contains(container)) {
            pickingOrder.getContainerList().add(container);
        }
        this.saveContainerBindingData(container.getContainerNo(), containerSubDto.getContainerSubNo(), lotId, itemId, outTaskDetail.getId(), pickNum, station.getStationId(), pickingOrder.getId());
        return true;
    }


    private void saveContainerBindingData(String containerNo, String containerNoSub, String lotId, String itemId, String outTaskDetailId, float pickNum, String stationId, String pickingOrderId) throws Exception {
        List<OutboundTaskBind> outboundTaskBindList = outboundTaskBindMapper.findByMap(MapUtils.put("containerNo", containerNo).getMap(), OutboundTaskBind.class);
        OutboundTaskBindDetail outboundTaskBindDetail = new OutboundTaskBindDetail();
        Timestamp nowDate = PrologDateUtils.parseObject(new Date());
        if (outboundTaskBindList.isEmpty()) {
            OutboundTaskBind outboundTaskBind = new OutboundTaskBind();
            outboundTaskBind.setContainerNo(containerNo);
            outboundTaskBind.setStationId(stationId);
            outboundTaskBind.setCreateTime(nowDate);
            outboundTaskBind.setPickingOrderId(pickingOrderId);
            outboundTaskBindMapper.save(outboundTaskBind);
            outboundTaskBindDetail.setOutbTaskBindId(outboundTaskBind.getId());
            outboundTaskBindDetail.setOutTaskDetailId(outTaskDetailId);
            outboundTaskBindDetail.setBindingNum(pickNum);
            outboundTaskBindDetail.setContainerNo(containerNo);
            outboundTaskBindDetail.setContainerNoSub(containerNoSub);
            outboundTaskBindDetail.setCreateTime(nowDate);
            outboundTaskBindDetail.setItemId(itemId);
            outboundTaskBindDetail.setLotId(lotId);
            outboundTaskBindDetailMapper.save(outboundTaskBindDetail);
        } else {
            outboundTaskBindDetail.setOutbTaskBindId(outboundTaskBindList.get(0).getId());
            outboundTaskBindDetail.setOutTaskDetailId(outTaskDetailId);
            outboundTaskBindDetail.setBindingNum(pickNum);
            outboundTaskBindDetail.setContainerNo(containerNo);
            outboundTaskBindDetail.setContainerNoSub(containerNoSub);
            outboundTaskBindDetail.setCreateTime(nowDate);
            outboundTaskBindDetail.setItemId(itemId);
            outboundTaskBindDetail.setLotId(lotId);
            outboundTaskBindDetailMapper.save(outboundTaskBindDetail);
        }
    }


    /**
     * ????????????????????????????????????
     *
     * @param stationDtos
     * @return
     */
    private StationDto getBestStation(List<StationDto> stationDtos) {
        if (stationDtos.isEmpty()) {
            return null;
        }
        StationDto bestStation = stationDtos.get(0);
        int bestmoveContainerCount = bestStation.computeContainerCount();
        for (int i = 1; i < stationDtos.size(); i++) {
            StationDto staion = stationDtos.get(i);
            int moveContainerCount = staion.computeContainerCount();
            if (moveContainerCount < bestmoveContainerCount) {
                bestmoveContainerCount = moveContainerCount;
                bestStation = staion;
            }
        }
        return bestStation;
    }
}
