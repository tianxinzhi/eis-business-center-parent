package com.prolog.eis.bc.service.osr.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.prolog.eis.bc.constant.OutboundStrategyConfigConstant;
import com.prolog.eis.bc.dao.OutboundTaskMapper;
import com.prolog.eis.bc.facade.dto.osr.OrderPoolMixDto;
import com.prolog.eis.bc.service.osr.OrderPoolMixTaskService;
import com.prolog.eis.bc.service.outboundtask.OutboundStrategyConfigService;
import com.prolog.eis.bc.service.outboundtask.OutboundTaskDetailService;
import com.prolog.eis.core.model.biz.outbound.OutboundTask;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskDetail;
import com.prolog.framework.core.restriction.FieldSelector;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: txz
 * @Date: 2021/10/11 16:47
 * @Desc: 实时汇总单汇单任务
 */
@Service
@Slf4j
public class OrderPoolMixTaskServiceImpl implements OrderPoolMixTaskService {

    @Autowired
    private OutboundTaskMapper taskMapper;
    @Autowired
    private OutboundTaskDetailService detailService;
    @Autowired
    private OutboundStrategyConfigService strategyConfigService;

    //订单池品或批集合
    private Map<Integer,Map<String, Set<String>>> odItemOrLotMap = Maps.newConcurrentMap();

//    @Scheduled(cron = "0/30 * * * * ?")
    @Transactional
    @Override
    public String geneOrderPoolToOutSummaryOrder() {
        try {
            //获取订单池出库的策略，判断为按品或按品批
            //List<OutboundStrategyConfig> configs = strategyConfigService.getByOutModel(OutboundStrategyConfigConstant.OUT_MODEL_ORDER_POOL);
            List<OrderPoolMixDto> orderPools = taskMapper.getOrderPoolNotStart(OutboundStrategyConfigConstant.OUT_MODEL_ORDER_POOL); //订单池订单
            //List<OrderPoolMixDto> summaryOrders = taskMapper.getNotFullSummaryOrder(OutboundStrategyConfigConstant.OUT_MODEL_ORDER_POOL);//汇总单
            List<OrderPoolMixDto> summaryOrders = taskMapper.getNotFullSummaryOrderByGroup(OutboundStrategyConfigConstant.OUT_MODEL_ORDER_POOL);//汇总单
            if (CollectionUtils.isNotEmpty(orderPools) && CollectionUtils.isNotEmpty(summaryOrders)) {
                //String typeNos = inAppend(configs.stream().map(s -> s.getTypeNo()).collect(Collectors.toList())); //typeNos
                //找订单池订单
                for (OrderPoolMixDto orderPool : orderPools) {
                    //找品批明细
                    Set<String> orderIts = getTaskDtlsByItemOrLot(orderPool.getMatchStrategy(), orderPool.getOutTaskId());
                    String outTaskId = orderPool.getOutTaskId();
                    //订单池品或批集合
                    Map<String, Set<String>> tarItems = new HashMap<>();
                    tarItems.put(outTaskId,orderIts);
                    //判断品批
                    int type = orderPool.getMatchStrategy();
                    if(null == odItemOrLotMap.get(type)){
                        odItemOrLotMap.put(type,tarItems);
                    }  else {
                        odItemOrLotMap.get(type).put(outTaskId,orderIts);
                    }
                }
                //汇总单汇入订单池订单
                for (OrderPoolMixDto summaryOrder : summaryOrders) {
                    //找品批明细
                    int type = summaryOrder.getMatchStrategy();
                    Set<String> batchs = getTaskDtlsByItemOrLot(type, summaryOrder.getOutTaskId());
                    //拿到重复度结果，按从高到低排序
                    List<String> maxEquals = findMaxEquals(batchs, odItemOrLotMap.get(type));
                    //汇总单加入订单池订单
                    long l = updateOrderTaskToSummary(maxEquals, summaryOrder);
                    if (l > 0) {
                        log.info(String.format("汇总单:%s 加入了%d条任务单",summaryOrder.getSmyId(),l));
                    }
                }
            }
            log.info("实时汇总单汇单完成");
            return "实时汇总单汇单完成";
        } catch (Exception e) {
            e.printStackTrace();
            return "实时汇总单汇单失败";
        }
    }

    /**
     * 汇总单添加订单池订单
     * @param maxEquals
     * @param summaryOrder
     * @return
     */
    public long updateOrderTaskToSummary(List<String> maxEquals,OrderPoolMixDto summaryOrder) throws IllegalAccessException {
        Iterator<String> iterator = maxEquals.iterator();
        //汇总单需要的订单数量
        int needOrderNum = summaryOrder.getMaxOrderNum() - summaryOrder.getOutTaskNum();
        List<String> btIds = new LinkedList<>();//任务单id
        long updSize = 0;
        while (iterator.hasNext()) {
            if (needOrderNum <= 0) {
                //填完当前汇总单,继续下个汇总单
                break;
            }
            String outTaskId = iterator.next();
            btIds.add(outTaskId);
            needOrderNum--;
            //移除已加入的以及对应订单池中的
            iterator.remove();
            odItemOrLotMap.get(summaryOrder.getMatchStrategy()).remove(outTaskId);
        }
        if (btIds.size() > 0) {
            Object[] objs = new Object[btIds.size()];
            for (int i = 0; i < btIds.size(); i++) {
                objs[i] = btIds.get(i);
            }

            List<OutboundTask> tasks = taskMapper.findByIds(OutboundTask.class, objs, FieldSelector.newInstance());
            //从订单池找单加入到汇总单
            updSize = joinToSummaryOrder(summaryOrder.getSmyId(), tasks);
        }
        return updSize;
    }

    /**
     * 任务单加入汇总单
     * @param smyId
     * @param tasks
     * @return
     */
    public long joinToSummaryOrder(String smyId, List<OutboundTask> tasks) {
        List<String> ids = new LinkedList<>();
        for (OutboundTask task : tasks) {
            ids.add(task.getId());
        }
        long l = taskMapper.updateBatch(smyId, appendIn(ids));
        return l;
    }


    public String appendIn(List<String> ids){
        StringBuilder str = new StringBuilder("");
        for (String id : ids) {
            str.append("'"+id+"',");
        }
        return str.substring(0,str.length() - 1);
    }

    /**
     * 返回商品集合或批次集合
     * @param type
     * @param outTaskId
     * @return
     */
    public Set<String> getTaskDtlsByItemOrLot(int type,String outTaskId){
        Set<String> itemOrLots = new HashSet<>();
        List<OutboundTaskDetail> taskDtls = detailService.getByOutTaskId(outTaskId);
        if (type == OutboundStrategyConfigConstant.STORE_MATCHING_STRATEGY_ITEM) {
            //按品
            itemOrLots.addAll(taskDtls.stream().map(OutboundTaskDetail::getItemId).collect(Collectors.toList()));
        } else if (type == OutboundStrategyConfigConstant.STORE_MATCHING_STRATEGY_IOT) {
            //按批
            itemOrLots.addAll(taskDtls.stream().map(OutboundTaskDetail::getLotId).collect(Collectors.toList()));
        }
        return itemOrLots;
    }

    /**
     * 找重复度最高，按重复度由高到低返回
     * @param sourceItems 汇总单已装品批集合
     * @param tarItems 订单池订单已有的品批集合（map key为任务单id，value为品批集合）
     * @return 任务单id
     */
    public List<String> findMaxEquals(Set<String> sourceItems, Map<String,Set<String>> tarItems){
        List<List<String>> cps = new LinkedList<>();
        for (Map.Entry<String, Set<String>> map : tarItems.entrySet()) {

            Sets.SetView intersection = Sets.intersection(sourceItems, map.getValue());//交集
            String taskId = map.getKey();
            int matchSize = intersection.size();//重合数
            int nonMatchSize = tarItems.size() - matchSize; //非重合数

            cps.add(Arrays.asList(taskId,matchSize+"",nonMatchSize+""));
        }
        //按重合数降序，非重合数升序排列，取第一个即为最优
        Collections.sort(cps,(a,b) -> {
            int a1 = Integer.valueOf(a.get(1));
            int a2 = Integer.valueOf(b.get(1));
            return a2 - a1;
        });
        cps = cps.stream().sorted(Comparator.comparing(l -> l.get(2))).collect(Collectors.toList());
        List<String> tasks = cps.stream().map(s -> s.get(0)).collect(Collectors.toList());
        return tasks;
    }
}
