package com.prolog.eis.bc.service.outboundtask.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.prolog.eis.bc.constant.OutboundStrategyConfigConstant;
import com.prolog.eis.bc.constant.OutboundTaskConstant;
import com.prolog.eis.bc.dao.OutboundTaskBindDtMapper;
import com.prolog.eis.bc.dao.OutboundTaskDetailMapper;
import com.prolog.eis.bc.dao.OutboundTaskMapper;
import com.prolog.eis.bc.facade.vo.OutboundStrategyConfigVo;
import com.prolog.eis.bc.feign.EisInvContainerStoreSubFeign;
import com.prolog.eis.bc.feign.EisWarehouseStationFeign;
import com.prolog.eis.bc.feign.container.EisContainerLocationFeign;
import com.prolog.eis.bc.service.outboundtask.ContainerOutDispatchService;
import com.prolog.eis.bc.service.outboundtask.OutboundStrategyConfigService;
import com.prolog.eis.bc.service.outboundtask.OutboundTaskBindDtService;
import com.prolog.eis.bc.service.outboundtask.OutboundTaskService;
import com.prolog.eis.bc.service.pickingorder.PickingOrderService;
import com.prolog.eis.common.util.MathHelper;
import com.prolog.eis.component.algorithm.composeorder.ComposeOrderUtils;
import com.prolog.eis.component.algorithm.composeorder.entity.BizOutTask;
import com.prolog.eis.component.algorithm.composeorder.entity.BizOutTaskDetail;
import com.prolog.eis.component.algorithm.composeorder.entity.PickingOrderDto;
import com.prolog.eis.component.algorithm.composeorder.entity.StationDto;
import com.prolog.eis.component.algorithm.composeorder.entity.WarehouseDto;
import com.prolog.eis.core.model.base.area.Station;
import com.prolog.eis.core.model.biz.outbound.OutboundTask;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskBindDetail;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskDetail;
import com.prolog.framework.common.message.RestMessage;
import com.prolog.framework.core.restriction.Criteria;
import com.prolog.framework.core.restriction.Restrictions;
import com.prolog.framework.utils.MapUtils;

import lombok.extern.slf4j.Slf4j;


/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/9/13
 **/
@Service
@Slf4j
public class OutboundTaskServiceImpl implements OutboundTaskService {

    @Autowired
    private ContainerOutDispatchService containerOutDispatchService;
    @Autowired
    private OutboundStrategyConfigService outboundStrategyConfigService;
    @Autowired
    private OutboundTaskBindDtService outboundTaskBindDtService;
    @Autowired
    private PickingOrderService pickingOrderService;

    @Autowired
    private OutboundTaskMapper outboundTaskMapper;
    @Autowired
    private OutboundTaskDetailMapper outboundTaskDetailMapper;
    @Autowired
    private OutboundTaskBindDtMapper outboundTaskBindDtMapper;

    @Autowired
    private EisWarehouseStationFeign eisWarehouseStationFeign;
    @Autowired
    private EisContainerLocationFeign eisContainerLocationFeign;
    @Autowired
    private EisInvContainerStoreSubFeign eisInvContainerStoreSubFeign;

    @Override
    public void composeAndGenerateOutbound() {
        /**
         * 1.数据初始化
         *      调用出库任务策略，查询任务类型
         * 2.调用组单算法
         *      根据出库任务策略，进行组单的算法,并生成拣选单任务
         * 3.调用出库调度
         *      根据出库任务策略，找托盘，并生成容器绑定任务以及生成容器搬运任务
         */
        OutboundStrategyConfigVo config = null;
        try {
            config = outboundStrategyConfigService.findConfigByTypeNo(OutboundStrategyConfigConstant.TYPE_B2C);
        } catch (Exception ex) {
            log.error("outboundStrategyConfigService.findConfigByTypeNo(B2C) throw excp", ex.toString());
        }
        if (null == config) {
            log.error("outboundStrategyConfigService.findConfigByTypeNo(B2C) return null");
            return;
        }
        String outModel = config.getOutModel();
        int storeMatchingStrategy = config.getStoreMatchingStrategy();
        String composeOrderConfig = config.getComposeOrderConfig();
        if (OutboundStrategyConfigConstant.OUT_MODEL_PICKING.equals(outModel)) {
            // 出单任务，从订单池获取biz_eis_out_task表 state=未开始
            List<BizOutTask> outTaskList = this.findAllNoStartTask();
            List<Station> stationList = null;
            // 站点，筛选出库站台，通过feign查询出所有的 isLock=0且claim=索取
            RestMessage<List<Station>> stationResp = null;
            try {
                stationResp = eisWarehouseStationFeign.findAllUnlockAndClaimStation();
            } catch (Exception e) {
                log.error("eisWarehouseStationFeign.findAllUnlockAndClaimStation() excp:{}", e.getMessage());
            }
            if (null != stationResp && stationResp.isSuccess()) {
                stationList = stationResp.getData();
            } else {
                log.error("eisWarehouseStationFeign.findAllUnlockAndClaimStation() return error, msg:{}",
                        null == stationResp ? "resp is null" : stationResp.getMessage());
            }
            if (CollectionUtils.isEmpty(stationList)) {
                log.error("eisWarehouseStationFeign.findAllUnlockAndClaimStation() return empty");
                return;
            }
            List<StationDto> stationDtoList = Lists.newArrayList();
            for (Station station : stationList) {
                StationDto stationDto = new StationDto();
                // 调用远程接口 查询sourceArea且targetArea=站点areaNo的容器数量
                RestMessage<Long> arriveLxCountResp = null;
                try {
                    arriveLxCountResp = eisContainerLocationFeign.findArriveLxCount(station.getAreaNo());
                } catch (Exception e) {
                    log.error("eisContainerLocationFeign.findArriveLxCount({}) excp:{}", station.getAreaNo(), e.getMessage());
                }
                if (null != arriveLxCountResp && arriveLxCountResp.isSuccess()) {
                    stationDto.setArriveLxCount(arriveLxCountResp.getData().intValue());
                } else {
                    log.error("eisContainerLocationFeign.findArriveLxCount() return error, areaNo:{}, msg:{}",
                            station.getAreaNo(), null == stationResp ? "resp is null" : stationResp.getMessage());
                    stationDto.setArriveLxCount(0);
                }
                // 调用远程接口 查询sourceArea!=站点areaNo且targetArea=站点areaNo的容器数量
                RestMessage<Long> chuKuLxCountResp = null;
                try {
                    chuKuLxCountResp = eisContainerLocationFeign.findChuKuLxCount(station.getAreaNo());
                } catch (Exception e) {
                    log.error("eisContainerLocationFeign.findChuKuLxCount({}) excp:{}", station.getAreaNo(), e.getMessage());
                }
                if (null != chuKuLxCountResp && chuKuLxCountResp.isSuccess()) {
                    stationDto.setChuKuLxCount(chuKuLxCountResp.getData().intValue());
                } else {
                    log.error("eisContainerLocationFeign.findChuKuLxCount() return error, areaNo:{}, msg:{}",
                            station.getAreaNo(), null == stationResp ? "resp is null" : stationResp.getMessage());
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
                stationDto.setPickingOrderList(pickingOrderDtoList);
                // 关联Need拣货单
                stationDto.setNeedPickingOrder(null);

                stationDtoList.add(stationDto);
            }

            WarehouseDto warehouse = new WarehouseDto();
            warehouse.setStationList(stationDtoList);
            Map<String, Integer> itemUseableMap = Maps.newHashMap();
            if (storeMatchingStrategy == OutboundStrategyConfigConstant.STORE_MATCHING_STRATEGY_IOT) {
                // 按批次出库
                RestMessage<Map<String, Integer>> itemStockMapResp = eisInvContainerStoreSubFeign.findSumQtyGroupByLotId();
                if (null != itemStockMapResp && itemStockMapResp.isSuccess()) {
                    Map<String, Integer> itemTotalMap = itemStockMapResp.getData();
                    Map<String, Integer> itemBindingMap = outboundTaskBindDtService.findSumBindingNumGroupByLotId();
                    for (String key : itemTotalMap.keySet()) {
                        itemUseableMap.put(key, MathHelper.getIntegerDiv(itemTotalMap.get(key), itemBindingMap.get(key)));
                    }
                } else {
                    log.error("eisInvContainerStoreSubFeign.findSumQtyGroupByLotId() return error, msg:{}",
                            null == stationResp ? "resp is null" : stationResp.getMessage());
                }
            } else if (storeMatchingStrategy == OutboundStrategyConfigConstant.STORE_MATCHING_STRATEGY_ITEM) {
                // 按商品出库
                RestMessage<Map<String, Integer>> itemStockMapResp = eisInvContainerStoreSubFeign.findSumQtyGroupByItemId();
                if (null != itemStockMapResp && itemStockMapResp.isSuccess()) {
                    Map<String, Integer> itemTotalMap = itemStockMapResp.getData();
                    Map<String, Integer> itemBindingMap = outboundTaskBindDtService.findSumBindingNumGroupByItemId();
                    for (String key : itemTotalMap.keySet()) {
                        itemUseableMap.put(key, MathHelper.getIntegerDiv(itemTotalMap.get(key), itemBindingMap.get(key)));
                    }
                } else {
                    log.error("eisInvContainerStoreSubFeign.findSumQtyGroupByItemId() return error, msg:{}",
                            null == stationResp ? "resp is null" : stationResp.getMessage());
                }
            } else {
                log.error("config.getStoreMatchingStrategy() is not LOT or ITEM, is:{}", config.getStoreMatchingStrategy());
                return;
            }
            warehouse.setItemStockMap(itemUseableMap);
            warehouse.setMaxItemCount(100);
            warehouse.setMaxPoolTaskNum(100);

            if (OutboundStrategyConfigConstant.ALGORITHM_COMPOSE_SIMILARITY.equals(composeOrderConfig)) {
                for (StationDto station : stationDtoList) {
                    // 筛选出最合适的任务
                    List<BizOutTask> bestBizOutTask = ComposeOrderUtils.compose(station, warehouse, outTaskList);
                    if (CollectionUtils.isEmpty(bestBizOutTask)) {
                        continue;
                    }
                    // 为站点分配拣选单
                    String pickingOrderId = null;
                    try {
                        pickingOrderId = pickingOrderService.insert(station.getStationId());
                    } catch (Exception e) {
                        log.error("pickingOrderService.insert, excp:{}", e.getMessage());
                    }
                    if (StringUtils.isEmpty(pickingOrderId)) {
                        continue;
                    }
                    List<String> taskIdList = bestBizOutTask.stream().map(BizOutTask::getId).collect(Collectors.toList());
                    try {
                        this.batchUpdatePickingOrderId(taskIdList, pickingOrderId);
                    } catch (Exception e) {
                        log.error("outboundTaskService.batchUpdatePickingOrderId, excp:{}", e.getMessage());
                    }
                }
                containerOutDispatchService.outContainerForPickingOrder(warehouse, config);
            }
        } else {
            log.error("config.getOutModel() is not PICKING_ORDER_OUT, is:{}", config.getOutModel());
        }
    }

    @Override
    public List<BizOutTask> findAllNoStartTask() {
        List<OutboundTask> taskList = outboundTaskMapper.findByMap(MapUtils
                .put("state", OutboundTaskConstant.STATE_NOSTART).getMap(),
                OutboundTask.class);
        return getBizOutTaskListByTaskList(taskList);
    }

    @Override
    public void batchUpdatePickingOrderId(List<String> idList,
            String pickingOrderId) throws Exception {
        if (CollectionUtils.isEmpty(idList)) {
            return;
        }
        if (null == pickingOrderId) {
            return;
        }
        Criteria crt = Criteria.forClass(OutboundTask.class);
        crt.setRestriction(Restrictions.in("id", idList.toArray()));
        outboundTaskMapper.updateMapByCriteria(MapUtils.put("pickingOrderId", pickingOrderId).put("startTime", new Date()).getMap(), crt);
    }

    @Override
    public List<BizOutTask> findByPickingOrderIdList(
            List<String> pickingOrderIdList) {
        if (CollectionUtils.isEmpty(pickingOrderIdList)) {
            return Lists.newArrayList();
        }
        Criteria crtTask = Criteria.forClass(OutboundTask.class);
        crtTask.setRestriction(Restrictions.in("pickingOrderId", pickingOrderIdList.toArray()));
        List<OutboundTask> taskList = outboundTaskMapper.findByCriteria(crtTask);
        return getBizOutTaskListByTaskList(taskList);
    }

    
    
    
    
    
    
    
    
    
    
    
    // ==========================================通用工具方法=====================================
    /**
     * 将数据库OutboundTask集合转化为BizOutTask集合
     * @param taskList 数据库OutboundTask集合
     * @return
     */
    private List<BizOutTask> getBizOutTaskListByTaskList(
            List<OutboundTask> taskList) {
        List<BizOutTask> bizOutTaskList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(taskList)) {
            return bizOutTaskList;
        }
        // 查询关联的task_detail数据
        List<String> taskIdList = taskList.stream().map(OutboundTask::getId).collect(Collectors.toList());
        List<OutboundTaskDetail> taskDtList = outboundTaskDetailMapper.find(taskIdList, null, null, null, null);
        // 查询关联的task_bind数据
        List<String> taskDetailIdList = taskDtList.stream().map(OutboundTaskDetail::getId).collect(Collectors.toList());
        List<OutboundTaskBindDetail> taskBindDtList = outboundTaskBindDtMapper.findSumBindingNumGroupByOutTaskDetailId(taskDetailIdList);
        for (OutboundTask task : taskList) {
            // 根据数据库数据生成对应业务对象
            BizOutTask bizTask = new BizOutTask();
            bizTask.setId(task.getId());
            bizTask.setPickOrderId(task.getPickingOrderId());
            bizTask.setPriority(task.getPriority());
            bizTask.setExpiryDate(task.getExpireDate());
            List<BizOutTaskDetail> bizOutTaskDetailList = Lists.newArrayList();
            for (OutboundTaskDetail taskDt : taskDtList) {
                if (null != task.getId() && task.getId().equals(taskDt.getOutTaskId())) {
                    // 数据库对象->生成对应业务对象
                    BizOutTaskDetail bizOutTaskDetail = new BizOutTaskDetail();
                    bizOutTaskDetail.setId(taskDt.getId());
                    bizOutTaskDetail.setItemId(taskDt.getItemId());
                    bizOutTaskDetail.setOutTaskId(taskDt.getOutTaskId());
                    bizOutTaskDetail.setPlanNum(taskDt.getPlanNum());
                    for (OutboundTaskBindDetail taskBindDt : taskBindDtList) {
                        if (bizOutTaskDetail.getId().equals(taskBindDt.getOutTaskDetailId())) {
                            bizOutTaskDetail.setBindingNum(taskBindDt.getBindingNum());
                            break;
                        }
                    }
                    bizOutTaskDetail.setActualNum(taskDt.getActualNum());
                    bizOutTaskDetailList.add(bizOutTaskDetail);
                }
            }
            bizTask.setBizOutTaskDetailList(bizOutTaskDetailList);
            bizOutTaskList.add(bizTask);
        }
        return bizOutTaskList;
    }

}
