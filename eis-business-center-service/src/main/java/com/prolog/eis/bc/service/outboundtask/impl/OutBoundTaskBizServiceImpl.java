package com.prolog.eis.bc.service.outboundtask.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
        // ????????????????????????
        int storeMatchingStrategy = config.getStoreMatchingStrategy();

        // ????????????????????????????????????feign?????????????????? isLock=0???claim=1??????
        List<Station> stationList = feignService.getAllUnlockAndClaimStation();
        List<StationDto> stationDtoList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(stationList)) {
            List<String> stationAreaNoList = stationList.stream().filter(e -> !StringUtils.isEmpty(e.getAreaNo())).map(Station::getAreaNo).collect(Collectors.toList());
            Map<String, StationDto> areaNoAndContainerCountMap = feignService.findAreaNoAndContainerCountMap(stationAreaNoList);
            for (Station station : stationList) {
                StationDto stationDto = new StationDto();
                StationDto containerCount = areaNoAndContainerCountMap.get(station.getAreaNo());
                if (null != containerCount) {
                    stationDto.setArriveLxCount(containerCount.getArriveLxCount());
                    stationDto.setChuKuLxCount(containerCount.getChuKuLxCount());
                } else {
                    stationDto.setArriveLxCount(0);
                    stationDto.setChuKuLxCount(0);
                }

                //TODO ??????????????????
                stationDto.setMaxLxCacheCount(station.getMaxCacheCount());
                stationDto.setMaxOrderNum(config.getMaxOrderNum());
                stationDto.setMaxOrderSpCount(100);
                stationDto.setStationId(station.getId());
                stationDto.setIsClaim(station.getClaim());
                stationDto.setIsLock(station.getIsLock());

                // ???????????????
                List<PickingOrderDto> pickingOrderDtoList = pickingOrderService.findByStationId(station.getId(), storeMatchingStrategy);

                log.error("pickingOrderService.findByStationId({},{}) return:{}", station.getId(), storeMatchingStrategy, JSONObject.toJSONString(pickingOrderDtoList));

                stationDto.setPickingOrderList(pickingOrderDtoList);
                // ??????Need?????????
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
            // ???????????????
            RestMessage<Map<String, Integer>> itemStockMapResp = null;
            try {
                itemStockMapResp = eisInvContainerStoreSubFeign.findSumQtyGroupByLotId();
            } catch (Exception e) {
                log.error("eisInvContainerStoreSubFeign.findSumQtyGroupByLotId() excp:{}", e.getMessage());
                throw e;
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
            // ???????????????
            RestMessage<Map<String, Integer>> itemStockMapResp = null;
            try {
                itemStockMapResp = eisInvContainerStoreSubFeign.findSumQtyGroupByItemId();
            } catch (Exception e) {
                log.error("eisInvContainerStoreSubFeign.findSumQtyGroupByItemId() excp:{}", e.getMessage());
                throw e;
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
