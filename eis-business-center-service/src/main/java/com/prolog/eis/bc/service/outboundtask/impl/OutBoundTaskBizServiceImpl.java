package com.prolog.eis.bc.service.outboundtask.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.prolog.eis.bc.constant.OutboundStrategyConfigConstant;
import com.prolog.eis.bc.facade.vo.OutboundStrategyConfigVo;
import com.prolog.eis.bc.feign.EisInvContainerStoreSubFeign;
import com.prolog.eis.bc.feign.EisWarehouseStationFeign;
import com.prolog.eis.bc.feign.container.EisContainerLocationFeign;
import com.prolog.eis.bc.service.outboundtask.OutBoundTaskBizService;
import com.prolog.eis.bc.service.outboundtask.OutboundTaskBindDtService;
import com.prolog.eis.bc.service.pickingorder.PickingOrderService;
import com.prolog.eis.common.util.MathHelper;
import com.prolog.eis.component.algorithm.composeorder.entity.PickingOrderDto;
import com.prolog.eis.component.algorithm.composeorder.entity.StationDto;
import com.prolog.eis.component.algorithm.composeorder.entity.WarehouseDto;
import com.prolog.eis.core.model.base.area.Station;
import com.prolog.framework.common.message.RestMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OutBoundTaskBizServiceImpl implements OutBoundTaskBizService {

    @Autowired
    private PickingOrderService pickingOrderService;

    @Autowired
    private OutboundTaskBindDtService outboundTaskBindDtService;

    @Autowired
    private EisWarehouseStationFeign eisWarehouseStationFeign;
    @Autowired
    private EisContainerLocationFeign eisContainerLocationFeign;
    @Autowired
    private EisInvContainerStoreSubFeign eisInvContainerStoreSubFeign;

    @Override
    public WarehouseDto getWarehouseByPickingOrderOutModel(OutboundStrategyConfigVo config) {
        // 获取库存匹配策略
        int storeMatchingStrategy = config.getStoreMatchingStrategy();

        // 站点，筛选出库站台，通过feign查询出所有的 isLock=0且claim=1索取
        List<Station> stationList = null;
        RestMessage<List<Station>> stationResp = null;
        try {
            stationResp = eisWarehouseStationFeign.findAllUnlockAndClaimStation();
        } catch (Exception e) {
            log.error("eisWarehouseStationFeign.findAllUnlockAndClaimStation() excp:{}", e.getMessage());
        }

        log.error("eisWarehouseStationFeign.findAllUnlockAndClaimStation() return:{}", JSONObject.toJSONString(stationResp));

        if (null != stationResp && stationResp.isSuccess()) {
            stationList = stationResp.getData();
        } else {
            log.error("eisWarehouseStationFeign.findAllUnlockAndClaimStation() return error, msg:{}",
                    null == stationResp ? "resp is null" : stationResp.getMessage());
        }
        List<StationDto> stationDtoList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(stationList)) {
            for (Station station : stationList) {
                StationDto stationDto = new StationDto();
                // 调用远程接口 查询sourceArea且targetArea=站点areaNo的容器数量
                RestMessage<Long> arriveLxCountResp = null;
                try {
                    arriveLxCountResp = eisContainerLocationFeign.findArriveLxCount(station.getAreaNo());
                } catch (Exception e) {
                    log.error("eisContainerLocationFeign.findArriveLxCount({}) excp:{}", station.getAreaNo(), e.getMessage());
                }

                log.error("eisContainerLocationFeign.findArriveLxCount({}) return:{}", station.getAreaNo(), JSONObject.toJSONString(arriveLxCountResp));

                if (null != arriveLxCountResp && arriveLxCountResp.isSuccess()) {
                    stationDto.setArriveLxCount(arriveLxCountResp.getData().intValue());
                } else {
                    log.error("eisContainerLocationFeign.findArriveLxCount() return error, areaNo:{}, msg:{}",
                            station.getAreaNo(), null == arriveLxCountResp ? "resp is null" : arriveLxCountResp.getMessage());
                    stationDto.setArriveLxCount(0);
                }
                // 调用远程接口 查询sourceArea!=站点areaNo且targetArea=站点areaNo的容器数量
                RestMessage<Long> chuKuLxCountResp = null;
                try {
                    chuKuLxCountResp = eisContainerLocationFeign.findChuKuLxCount(station.getAreaNo());
                } catch (Exception e) {
                    log.error("eisContainerLocationFeign.findChuKuLxCount({}) excp:{}", station.getAreaNo(), e.getMessage());
                }

                log.error("eisContainerLocationFeign.findChuKuLxCount({}) return:{}", station.getAreaNo(), JSONObject.toJSONString(chuKuLxCountResp));

                if (null != chuKuLxCountResp && chuKuLxCountResp.isSuccess()) {
                    stationDto.setChuKuLxCount(chuKuLxCountResp.getData().intValue());
                } else {
                    log.error("eisContainerLocationFeign.findChuKuLxCount() return error, areaNo:{}, msg:{}",
                            station.getAreaNo(), null == chuKuLxCountResp ? "resp is null" : chuKuLxCountResp.getMessage());
                    stationDto.setChuKuLxCount(0);
                }
                stationDto.setMaxLxCacheCount(100);
                stationDto.setMaxOrderNum(config.getMaxOrderNum());
                stationDto.setMaxOrderSpCount(100);
                stationDto.setStationId(station.getId());
                stationDto.setIsClaim(station.getClaim());
                stationDto.setIsLock(station.getIsLock());

                // 关联拣货单
                List<PickingOrderDto> pickingOrderDtoList = pickingOrderService.findByStationId(station.getId(), storeMatchingStrategy);

                log.error("pickingOrderService.findByStationId({},{}) return:{}", station.getId(), storeMatchingStrategy, JSONObject.toJSONString(pickingOrderDtoList));

                stationDto.setPickingOrderList(pickingOrderDtoList);
                // 关联Need拣货单
                stationDto.setNeedPickingOrder(null);
                stationDtoList.add(stationDto);
            }
        } else {
            log.error("eisWarehouseStationFeign.findAllUnlockAndClaimStation() return empty");
        }

        WarehouseDto warehouse = new WarehouseDto();
        warehouse.setStationList(stationDtoList);
        Map<String, Integer> itemUseableMap = Maps.newHashMap();
        if (storeMatchingStrategy == OutboundStrategyConfigConstant.STORE_MATCHING_STRATEGY_IOT) {
            // 按批次出库
            RestMessage<Map<String, Integer>> itemStockMapResp = null;
            try {
                itemStockMapResp = eisInvContainerStoreSubFeign.findSumQtyGroupByLotId();
            } catch (Exception e) {
                log.error("eisInvContainerStoreSubFeign.findSumQtyGroupByLotId() excp:{}", e.getMessage());
            }

            log.error("eisInvContainerStoreSubFeign.findSumQtyGroupByLotId() return:{}", JSONObject.toJSONString(itemStockMapResp));

            if (null != itemStockMapResp && itemStockMapResp.isSuccess()) {
                Map<String, Integer> itemTotalMap = itemStockMapResp.getData();
                Map<String, Integer> itemBindingMap = outboundTaskBindDtService.findSumBindingNumGroupByLotId();

                log.error("outboundTaskBindDtService.findSumBindingNumGroupByLotId() return:{}", JSONObject.toJSONString(itemBindingMap));

                for (String key : itemTotalMap.keySet()) {
                    itemUseableMap.put(key, MathHelper.getIntegerDiv(itemTotalMap.get(key), itemBindingMap.get(key)));
                }
            } else {
                log.error("eisInvContainerStoreSubFeign.findSumQtyGroupByLotId() return error, msg:{}",
                        null == itemStockMapResp ? "resp is null" : itemStockMapResp.getMessage());
            }
        } else if (storeMatchingStrategy == OutboundStrategyConfigConstant.STORE_MATCHING_STRATEGY_ITEM) {
            // 按商品出库
            RestMessage<Map<String, Integer>> itemStockMapResp = null;
            try {
                itemStockMapResp = eisInvContainerStoreSubFeign.findSumQtyGroupByItemId();
            } catch (Exception e) {
                log.error("eisInvContainerStoreSubFeign.findSumQtyGroupByItemId() excp:{}", e.getMessage());
            }

            log.error("eisInvContainerStoreSubFeign.findSumQtyGroupByItemId() return:{}", JSONObject.toJSONString(itemStockMapResp));

            if (null != itemStockMapResp && itemStockMapResp.isSuccess()) {
                Map<String, Integer> itemTotalMap = itemStockMapResp.getData();
                Map<String, Integer> itemBindingMap = outboundTaskBindDtService.findSumBindingNumGroupByItemId();

                log.error("outboundTaskBindDtService.findSumBindingNumGroupByItemId() return:{}", JSONObject.toJSONString(itemBindingMap));

                for (String key : itemTotalMap.keySet()) {
                    itemUseableMap.put(key, MathHelper.getIntegerDiv(itemTotalMap.get(key), itemBindingMap.get(key)));
                }
            } else {
                log.error("eisInvContainerStoreSubFeign.findSumQtyGroupByItemId() return error, msg:{}",
                        null == itemStockMapResp ? "resp is null" : itemStockMapResp.getMessage());
            }
        } else {
            log.error("config.getStoreMatchingStrategy() is not LOT or ITEM, is:{}", config.getStoreMatchingStrategy());
        }
        warehouse.setItemStockMap(itemUseableMap);
        warehouse.setMaxItemCount(100);
        warehouse.setMaxPoolTaskNum(100);
        return warehouse;
    }

}
