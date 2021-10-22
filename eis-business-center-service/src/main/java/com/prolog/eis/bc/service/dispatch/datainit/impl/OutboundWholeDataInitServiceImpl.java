package com.prolog.eis.bc.service.dispatch.datainit.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
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
        // 塞入出库任务数据
        List<OutTaskAlgorithmDto> outTaskAlgorithmDtoList = Lists.newArrayList();
        // 根据配置找到对应的出库任务，查询出必要信息
        // 查询typeNo在集合中的且状态=未开始or进行中的出库任务
        List<BizOutTask> outboundTaskList = outboundTaskService
                .getListByTypeNoListAndStateList(Lists.newArrayList(config.getTypeNo()),
                        Lists.newArrayList(OutboundTaskConstant.STATE_NOSTART,
                                OutboundTaskConstant.STATE_GOINGON));
        log.error("outboundTaskService.getListByTypeNoListAndStateList() return:{}", JSONObject.toJSONString(outboundTaskList));
        // 查询策略对应的出库任务单类型编号列表
        if (CollectionUtils.isEmpty(outboundTaskList)) {
            return result;
        }
        for (BizOutTask outboundTask : outboundTaskList) {
            OutTaskAlgorithmDto outTaskAlgorithmDto = new OutTaskAlgorithmDto();
            outTaskAlgorithmDto.setOutTaskId(outboundTask.getId());
            // 取出该outTaskId下的出库单明细
            List<BizOutTaskDetail> outboundTaskDetailListByOutTaskId = outboundTask.getBizOutTaskDetailList();
            if (CollectionUtils.isEmpty(outboundTaskDetailListByOutTaskId)) {
                outTaskAlgorithmDto.setOutTaskDetailList(Lists.newArrayList());
            } else {
                List<OutTaskDetailAlgorithmDto> outTaskDetailAlgorithmDtoList = Lists.newArrayList();
                // 将BizOutTaskDetail转化为OutTaskDetailAlgorithmDto
                for (BizOutTaskDetail outboundTaskDetail : outboundTaskDetailListByOutTaskId) {
                    OutTaskDetailAlgorithmDto outTaskDetailAlgorithmDto = new OutTaskDetailAlgorithmDto();
                    outTaskDetailAlgorithmDto.setOutTaskId(outboundTaskDetail.getOutTaskId());
                    outTaskDetailAlgorithmDto.setOutTaskDetailId(outboundTaskDetail.getId());
                    outTaskDetailAlgorithmDto.setPlanNum(outboundTaskDetail.getPlanNum());
                    if (config.getStoreMatchingStrategy() == OutboundStrategyConfigConstant.STORE_MATCHING_STRATEGY_IOT) {
                        // 按批次
                        outTaskDetailAlgorithmDto.setUniqueKey(outboundTaskDetail.getLotId());
                    } else {
                        // 按商品
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

        // 塞入库存数据
        List<InvStockAlgorithmDto> invStockAlgorithmDtoList = Lists.newArrayList();
        List<OutboundStrategySourceAreaConfig> saConfigList = config.getOutboundStrategySourceAreaConfigList();
        if (!CollectionUtils.isEmpty(saConfigList)) {
            // 获取配置中的区域信息集合
            List<String> areaNoList = saConfigList.stream().filter(e -> !StringUtils.isEmpty(e.getAreaNo())).map(e -> e.getAreaNo()).collect(Collectors.toList());
            // 根据区域信息查询对应空闲托盘
            List<ContainerLocation> containerList = feignService.getAllFreeContainerNoByAreaNo(areaNoList);
            // 筛选托盘号
            List<String> containerNoList = containerList.stream().map(ContainerLocation::getContainerNo).collect(Collectors.toList());
            // 根据托盘号查询托盘库存
            Map<String, EisInvContainerStoreVo> containerNoAndStoreMap = feignService.getInvContainerStoreListByContainerNoList(containerNoList);
            // 筛选托盘位置
            List<String> containerSourceLocationList = containerList.stream().filter(e -> !StringUtils.isEmpty(e.getSourceLocation())).map(ContainerLocation::getSourceLocation).collect(Collectors.toList());
            // 根据托盘位置查询位置XYZ，DEPT等数据
            Map<String, WhLocatorDto> locationNoAndLocatorMap = feignService.getWhLocatorListByLocationNoList(containerSourceLocationList);
            for (ContainerLocation containerLocation : containerList) {
                // 取出托盘号containerNo对应的容器库存信息
                EisInvContainerStoreVo storeVo = containerNoAndStoreMap.get(containerLocation.getContainerNo());
                if (null != storeVo) {
                    // 数据库托盘库存对象EisInvContainerStoreVo->业务对象InvStockAlgorithmDto
                    InvStockAlgorithmDto invStockAlgorithmDto = new InvStockAlgorithmDto();
                    invStockAlgorithmDto.setContainerNo(containerLocation.getContainerNo());

                    List<InvStockDetailAlgorithmDto> invStockDetailAlgorithmDtoList = Lists.newArrayList();
                    if (!CollectionUtils.isEmpty(storeVo.getContainerStoreSubList())) {
                        for (EisInvContainerStoreSubVo subStoreVo : storeVo.getContainerStoreSubList()) {
                            InvStockDetailAlgorithmDto invStockDetailAlgorithmDto = new InvStockDetailAlgorithmDto();
                            if (config.getStoreMatchingStrategy() == OutboundStrategyConfigConstant.STORE_MATCHING_STRATEGY_IOT) {
                                // 按批次
                                invStockDetailAlgorithmDto.setUniqueKey(subStoreVo.getLotId());
                                invStockAlgorithmDto.setUniqueKeyAndQtyMap(storeVo.calSubStoreSumQtyGroupByLotId());
                                invStockAlgorithmDto.setUniqueKeySet(storeVo.calSubStoreSumQtyGroupByLotId().keySet());
                            } else {
                                // 按商品
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
                    // 取出LocationNo对应的位置详情
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

        // 塞入站点数据
        List<WholeStationDto> wholeStationDtoList = Lists.newArrayList();
        // 站点，筛选出库站台，通过feign查询出所有的 isLock=0且claim=1索取
        List<Station> stationList = feignService.getAllUnlockAndClaimStation();
        if (!CollectionUtils.isEmpty(stationList)) {
            for (Station station : stationList) {
                // 将数据库对象Station->业务对象WholeStationDto
                WholeStationDto wholeStationDto = new WholeStationDto();
                wholeStationDto.setArriveLxCount(feignService.getFreeContainerCount(station.getAreaNo()));
                wholeStationDto.setChuKuLxCount(feignService.getChuKuContainerCount(station.getAreaNo()));
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
