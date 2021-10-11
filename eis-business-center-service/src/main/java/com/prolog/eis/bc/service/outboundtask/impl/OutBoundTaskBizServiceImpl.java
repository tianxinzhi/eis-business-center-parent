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
            stationResp = JSONObject.parseObject("{\"code\":\"200\",\"data\":[{\"areaNo\":\"S020101\",\"claim\":1,\"createTime\":1631244096000,\"id\":\"633665508595601408\",\"isLock\":0,\"stationNo\":\"20101\",\"type\":3},{\"areaNo\":\"S020102\",\"claim\":1,\"createTime\":1631244109000,\"id\":\"633665565474557952\",\"isLock\":0,\"stationNo\":\"20102\",\"type\":1},{\"areaNo\":\"S020103\",\"claim\":1,\"createTime\":1631244125000,\"id\":\"633665631182524416\",\"isLock\":0,\"stationNo\":\"20103\",\"type\":1},{\"areaNo\":\"S020104\",\"claim\":1,\"createTime\":1631244138000,\"id\":\"633665686417313792\",\"isLock\":0,\"stationNo\":\"20104\",\"type\":1},{\"areaNo\":\"6789\",\"claim\":1,\"createTime\":1631529073000,\"id\":\"634860790171701248\",\"isLock\":0,\"stationNo\":\"68876\",\"type\":1},{\"areaNo\":\"test02\",\"claim\":1,\"createTime\":1632276348000,\"id\":\"637995087682473984\",\"isLock\":0,\"stationNo\":\"Test02\",\"type\":1}],\"message\":\"success\",\"success\":true}", RestMessage.class);
            //throw e;
        }

        log.error("eisWarehouseStationFeign.findAllUnlockAndClaimStation() return:{}", JSONObject.toJSONString(stationResp));

        if (null != stationResp && stationResp.isSuccess()) {
            stationList = stationResp.getData();
        } else {
            String message = null == stationResp ? "resp is null" : stationResp.getMessage();
            log.error("eisWarehouseStationFeign.findAllUnlockAndClaimStation() return error, msg:{}", message);
            throw new BizException(message);
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
                    arriveLxCountResp = JSONObject.parseObject("{\"code\":\"200\",\"data\":0L,\"message\":\"操作成功\",\"success\":true}", RestMessage.class);
//                    throw e;
                }

                log.error("eisContainerLocationFeign.findArriveLxCount({}) return:{}", station.getAreaNo(), JSONObject.toJSONString(arriveLxCountResp));

                if (null != arriveLxCountResp && arriveLxCountResp.isSuccess()) {
                    stationDto.setArriveLxCount(arriveLxCountResp.getData().intValue());
                } else {
                    String message = null == arriveLxCountResp ? "resp is null" : arriveLxCountResp.getMessage();
                    log.error("eisContainerLocationFeign.findArriveLxCount({}) return error, msg:{}", station.getAreaNo(), message);
                    stationDto.setArriveLxCount(0);
                }
                // 调用远程接口 查询sourceArea!=站点areaNo且targetArea=站点areaNo的容器数量
                RestMessage<Long> chuKuLxCountResp = null;
                try {
                    chuKuLxCountResp = eisContainerLocationFeign.findChuKuLxCount(station.getAreaNo());
                } catch (Exception e) {
                    log.error("eisContainerLocationFeign.findChuKuLxCount({}) excp:{}", station.getAreaNo(), e.getMessage());
                    chuKuLxCountResp = JSONObject.parseObject("{\"code\":\"200\",\"data\":0L,\"message\":\"操作成功\",\"success\":true}", RestMessage.class);
//                    throw e;
                }

                log.error("eisContainerLocationFeign.findChuKuLxCount({}) return:{}", station.getAreaNo(), JSONObject.toJSONString(chuKuLxCountResp));

                if (null != chuKuLxCountResp && chuKuLxCountResp.isSuccess()) {
                    stationDto.setChuKuLxCount(chuKuLxCountResp.getData().intValue());
                } else {
                    String message = null == chuKuLxCountResp ? "resp is null" : chuKuLxCountResp.getMessage();
                    log.error("eisContainerLocationFeign.findChuKuLxCount({}) return error, msg:{}", station.getAreaNo(), message);
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
