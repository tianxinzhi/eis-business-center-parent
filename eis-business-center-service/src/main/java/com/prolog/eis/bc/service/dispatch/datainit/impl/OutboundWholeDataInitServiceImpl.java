package com.prolog.eis.bc.service.dispatch.datainit.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.prolog.eis.bc.constant.OutboundStrategyConfigConstant;
import com.prolog.eis.bc.constant.OutboundTaskConstant;
import com.prolog.eis.bc.facade.dto.outbound.WholeOutTaskContainerDto;
import com.prolog.eis.bc.facade.dto.outbound.WholeStationDto;
import com.prolog.eis.bc.facade.vo.OutboundStrategyConfigVo;
import com.prolog.eis.bc.service.FeignService;
import com.prolog.eis.bc.service.dispatch.datainit.OutboundWholeDataInitService;
import com.prolog.eis.bc.service.outboundtask.OutboundTaskService;
import com.prolog.eis.component.algorithm.composeorder.entity.BizOutTask;
import com.prolog.eis.component.algorithm.composeorder.entity.BizOutTaskDetail;
import com.prolog.eis.component.algorithm.composeorder.entity.StationDto;
import com.prolog.eis.core.dto.route.WhLocatorDto;
import com.prolog.eis.core.model.base.area.Station;
import com.prolog.eis.core.model.biz.route.ContainerLocation;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategySourceAreaConfig;
import com.prolog.upcloud.base.inventory.vo.EisInvContainerStoreSubVo;
import com.prolog.upcloud.base.inventory.vo.EisInvContainerStoreVo;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.InvStockAlgorithmDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.InvStockDetailAlgorithmDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.OutTaskAlgorithmDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.OutTaskDetailAlgorithmDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.WholeOutContainerDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/10/21
 **/
@Service
@Slf4j
public class OutboundWholeDataInitServiceImpl implements OutboundWholeDataInitService {

    @Autowired
    private OutboundTaskService outboundTaskService;

    @Autowired
    private FeignService feignService;

    @Override
    public WholeOutContainerDto findWholeOutData() {


        return null;
    }

    @Override
    public WholeOutTaskContainerDto findWholeOutData(OutboundStrategyConfigVo config) {
        if (null == config) {
            return null;
        }
        if (StringUtils.isEmpty(config.getTypeNo())) {
            return null;
        }
        if (config.getOutType() != OutboundStrategyConfigConstant.OUT_TYPE_WHOLE) {
            return null;
        }
        WholeOutTaskContainerDto result = new WholeOutTaskContainerDto();
        result.setInvStockAlgorithmDtoList(Lists.newArrayList());
        result.setOutTaskAlgorithmDtoList(Lists.newArrayList());;
        // ????????????????????????
        List<OutTaskAlgorithmDto> outTaskAlgorithmDtoList = Lists.newArrayList();
        // ???????????????????????????????????????????????????????????????
        // ??????typeNo????????????????????????=?????????or????????????????????????
        List<BizOutTask> outboundTaskList = outboundTaskService
                .getListByTypeNoListAndStateList(Lists.newArrayList(config.getTypeNo()),
                        Lists.newArrayList(OutboundTaskConstant.STATE_NOSTART,
                                OutboundTaskConstant.STATE_GOINGON));

        // ??????????????????????????????????????????????????????
        if (CollectionUtils.isEmpty(outboundTaskList)) {
            return result;
        }
        for (BizOutTask outboundTask : outboundTaskList) {
            OutTaskAlgorithmDto outTaskAlgorithmDto = new OutTaskAlgorithmDto();
            outTaskAlgorithmDto.setOutTaskId(outboundTask.getId());
            // ?????????outTaskId?????????????????????
            List<BizOutTaskDetail> outboundTaskDetailListByOutTaskId = outboundTask.getBizOutTaskDetailList();
            if (CollectionUtils.isEmpty(outboundTaskDetailListByOutTaskId)) {
                outTaskAlgorithmDto.setOutTaskDetailList(Lists.newArrayList());
            } else {
                List<OutTaskDetailAlgorithmDto> outTaskDetailAlgorithmDtoList = Lists.newArrayList();
                // ???BizOutTaskDetail?????????OutTaskDetailAlgorithmDto
                for (BizOutTaskDetail outboundTaskDetail : outboundTaskDetailListByOutTaskId) {
                    OutTaskDetailAlgorithmDto outTaskDetailAlgorithmDto = new OutTaskDetailAlgorithmDto();
                    outTaskDetailAlgorithmDto.setOutTaskId(outboundTaskDetail.getOutTaskId());
                    outTaskDetailAlgorithmDto.setOutTaskDetailId(outboundTaskDetail.getId());
                    outTaskDetailAlgorithmDto.setPlanNum(outboundTaskDetail.getPlanNum());
                    if (config.getStoreMatchingStrategy() == OutboundStrategyConfigConstant.STORE_MATCHING_STRATEGY_IOT) {
                        // ?????????
                        outTaskDetailAlgorithmDto.setUniqueKey(outboundTaskDetail.getLotId());
                    } else {
                        // ?????????
                        outTaskDetailAlgorithmDto.setUniqueKey(outboundTaskDetail.getItemId());
                    }
                    outTaskDetailAlgorithmDto.setActualNum(outboundTaskDetail.getActualNum());
                    outTaskDetailAlgorithmDto.setBindingNum(outboundTaskDetail.getBindingNum());
                    outTaskDetailAlgorithmDtoList.add(outTaskDetailAlgorithmDto);
                }
                outTaskAlgorithmDto.setOutTaskDetailList(outTaskDetailAlgorithmDtoList);
            }
            outTaskAlgorithmDtoList.add(outTaskAlgorithmDto);
        }

        // ??????????????????
        List<InvStockAlgorithmDto> invStockAlgorithmDtoList = Lists.newArrayList();
        List<OutboundStrategySourceAreaConfig> saConfigList = config.getOutboundStrategySourceAreaConfigList();
        if (!CollectionUtils.isEmpty(saConfigList)) {
            // ????????????????????????????????????
            List<String> areaNoList = saConfigList.stream().filter(e -> !StringUtils.isEmpty(e.getAreaNo())).map(e -> e.getAreaNo()).collect(Collectors.toList());
            // ??????????????????????????????????????????
            List<ContainerLocation> containerList = feignService.getAllFreeContainerNoByAreaNo(areaNoList);
            // ???????????????
            List<String> containerNoList = containerList.stream().map(ContainerLocation::getContainerNo).collect(Collectors.toList());
            // ?????????????????????????????????
            Map<String, EisInvContainerStoreVo> containerNoAndStoreMap = feignService.getInvContainerStoreListByContainerNoList(containerNoList);
            // ??????????????????
            List<String> containerSourceLocationList = containerList.stream().filter(e -> !StringUtils.isEmpty(e.getSourceLocation())).map(ContainerLocation::getSourceLocation).collect(Collectors.toList());
            // ??????????????????????????????XYZ???DEPT?????????
            Map<String, WhLocatorDto> locationNoAndLocatorMap = feignService.getWhLocatorListByLocationNoList(containerSourceLocationList);
            for (ContainerLocation containerLocation : containerList) {
                // ???????????????containerNo???????????????????????????
                EisInvContainerStoreVo storeVo = containerNoAndStoreMap.get(containerLocation.getContainerNo());
                if (null != storeVo) {
                    // ???????????????????????????EisInvContainerStoreVo->????????????InvStockAlgorithmDto
                    InvStockAlgorithmDto invStockAlgorithmDto = new InvStockAlgorithmDto();
                    invStockAlgorithmDto.setContainerNo(containerLocation.getContainerNo());

                    List<InvStockDetailAlgorithmDto> invStockDetailAlgorithmDtoList = Lists.newArrayList();
                    if (!CollectionUtils.isEmpty(storeVo.getContainerStoreSubList())) {
                        for (EisInvContainerStoreSubVo subStoreVo : storeVo.getContainerStoreSubList()) {
                            InvStockDetailAlgorithmDto invStockDetailAlgorithmDto = new InvStockDetailAlgorithmDto();
                            if (config.getStoreMatchingStrategy() == OutboundStrategyConfigConstant.STORE_MATCHING_STRATEGY_IOT) {
                                // ?????????
                                invStockDetailAlgorithmDto.setUniqueKey(subStoreVo.getLotId());
                                invStockAlgorithmDto.setUniqueKeyAndQtyMap(storeVo.calSubStoreSumQtyGroupByLotId());
                                invStockAlgorithmDto.setUniqueKeySet(storeVo.calSubStoreSumQtyGroupByLotId().keySet());
                            } else {
                                // ?????????
                                invStockDetailAlgorithmDto.setUniqueKey(subStoreVo.getItemId());
                                invStockAlgorithmDto.setUniqueKeyAndQtyMap(storeVo.calSubStoreSumQtyGroupByItemId());
                                invStockAlgorithmDto.setUniqueKeySet(storeVo.calSubStoreSumQtyGroupByItemId().keySet());
                            }
                            invStockDetailAlgorithmDto.setContainerNo(storeVo.getContainerNo());
                            invStockDetailAlgorithmDto.setContainerSubNo(subStoreVo.getContainerStoreSubNo());
                            invStockDetailAlgorithmDto.setQty((float) subStoreVo.getQty());
                            invStockDetailAlgorithmDtoList.add(invStockDetailAlgorithmDto);
                        }
                    }
                    invStockAlgorithmDto.setInvStockDetailAlgorithmDtoList(invStockDetailAlgorithmDtoList);
                    // ??????LocationNo?????????????????????
                    WhLocatorDto locator = locationNoAndLocatorMap.get(containerLocation.getSourceLocation());
                    if (null != locator) {
                        invStockAlgorithmDto.setDeptNum(locator.getDeptNum());
                        invStockAlgorithmDto.setX(locator.getX());
                        invStockAlgorithmDto.setY(locator.getY());
                        invStockAlgorithmDto.setLayer(locator.getLayer());
                    }
                    invStockAlgorithmDtoList.add(invStockAlgorithmDto);
                }
            }
        }

        // ??????????????????
        List<WholeStationDto> wholeStationDtoList = Lists.newArrayList();
        // ????????????????????????????????????feign?????????????????? isLock=0???claim=1??????
        List<Station> stationList = feignService.getAllUnlockAndClaimStation();
        if (!CollectionUtils.isEmpty(stationList)) {
            List<String> stationAreaNoList = stationList.stream().filter(e -> !StringUtils.isEmpty(e.getAreaNo())).map(Station::getAreaNo).collect(Collectors.toList());
            Map<String, StationDto> areaNoAndContainerCountMap = feignService.findAreaNoAndContainerCountMap(stationAreaNoList);
            for (Station station : stationList) {
                // ??????????????????Station->????????????WholeStationDto
                WholeStationDto wholeStationDto = new WholeStationDto();
                StationDto containerCount = areaNoAndContainerCountMap.get(station.getAreaNo());
                if (null != containerCount) {
                    wholeStationDto.setArriveLxCount(containerCount.getArriveLxCount());
                    wholeStationDto.setChuKuLxCount(containerCount.getChuKuLxCount());
                } else {
                    wholeStationDto.setArriveLxCount(0);
                    wholeStationDto.setChuKuLxCount(0);
                }
                wholeStationDto.setAreaNo(station.getAreaNo());
                wholeStationDto.setIsLock(station.getIsLock());
                wholeStationDto.setIsClaim(station.getClaim());
                wholeStationDto.setStationId(station.getId());
                wholeStationDto.setMaxLxCacheCount(station.getMaxCacheCount());
                wholeStationDtoList.add(wholeStationDto);
            }
        }
        result.setOutTaskAlgorithmDtoList(outTaskAlgorithmDtoList);
        result.setInvStockAlgorithmDtoList(invStockAlgorithmDtoList);
        result.setWholeStationDtoList(wholeStationDtoList);
        return result;
    }
}
