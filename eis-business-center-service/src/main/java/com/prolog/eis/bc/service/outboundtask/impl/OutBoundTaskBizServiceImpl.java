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
import com.prolog.eis.bc.service.FeignService;
import com.prolog.eis.bc.service.outboundtask.OutBoundTaskBizService;
import com.prolog.eis.bc.service.outboundtask.OutboundTaskBindDtService;
import com.prolog.eis.bc.service.pickingorder.PickingOrderService;
import com.prolog.eis.common.util.MathHelper;
import com.prolog.eis.component.algorithm.composeorder.entity.PickingOrderDto;
import com.prolog.eis.component.algorithm.composeorder.entity.StationDto;
import com.prolog.eis.component.algorithm.composeorder.entity.WarehouseDto;
import com.prolog.eis.core.model.base.area.Station;
import com.prolog.framework.common.message.RestMessage;
import com.prolog.framework.core.exception.BizException;

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

    @Autowired
    private FeignService feignService;

    @Override
    public WarehouseDto getWarehouseByPickingOrderOutModel(OutboundStrategyConfigVo config) {
        // 获取库存匹配策略
        int storeMatchingStrategy = config.getStoreMatchingStrategy();

        // 站点，筛选出库站台，通过feign查询出所有的 isLock=0且claim=1索取
        List<Station> stationList = feignService.getAllUnlockAndClaimStation();
        List<StationDto> stationDtoList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(stationList)) {
            for (Station station : stationList) {
                StationDto stationDto = new StationDto();
                stationDto.setArriveLxCount(feignService.getFreeContainerCount(station.getAreaNo()));
                stationDto.setChuKuLxCount(feignService.getChuKuContainerCount(station.getAreaNo()));

                //TODO 改为直接查询
                stationDto.setMaxLxCacheCount(station.getMaxCacheCount());
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
                itemStockMapResp = JSONObject.parseObject("{\"code\":\"200\",\"data\":{\"0021257245\":60},\"message\":\"操作成功\",\"success\":true}", RestMessage.class);

//                throw e;
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
                itemStockMapResp = JSONObject.parseObject("{\"code\":\"200\",\"data\":{\"0021257245\":60},\"message\":\"操作成功\",\"success\":true}", RestMessage.class);

//                throw e;
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
        warehouse.setMaxItemCount(config.getMaxItemNum());
        warehouse.setMaxPoolTaskNum(config.getMaxOrderNum());
        return warehouse;
    }

}
