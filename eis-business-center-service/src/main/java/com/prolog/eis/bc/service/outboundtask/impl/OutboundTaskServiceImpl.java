package com.prolog.eis.bc.service.outboundtask.impl;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.prolog.eis.bc.constant.OutboundStrategyConfigConstant;
import com.prolog.eis.bc.constant.OutboundTaskConstant;
import com.prolog.eis.bc.dao.OutboundTaskBindDetailMapper;
import com.prolog.eis.bc.dao.OutboundTaskDetailMapper;
import com.prolog.eis.bc.dao.OutboundTaskMapper;
import com.prolog.eis.bc.facade.vo.OutboundStrategyConfigVo;
import com.prolog.eis.bc.service.businesscenter.OutboundTaskReportService;
import com.prolog.eis.bc.service.outboundtask.ContainerOutDispatchService;
import com.prolog.eis.bc.service.outboundtask.OutBoundTaskBizService;
import com.prolog.eis.bc.service.outboundtask.OutboundStrategyConfigService;
import com.prolog.eis.bc.service.outboundtask.OutboundTaskDetailHistoryService;
import com.prolog.eis.bc.service.outboundtask.OutboundTaskHistoryService;
import com.prolog.eis.bc.service.outboundtask.OutboundTaskService;
import com.prolog.eis.bc.service.pickingorder.PickingOrderService;
import com.prolog.eis.common.util.MathHelper;
import com.prolog.eis.component.algorithm.composeorder.ComposeOrderUtils;
import com.prolog.eis.component.algorithm.composeorder.entity.BizOutTask;
import com.prolog.eis.component.algorithm.composeorder.entity.BizOutTaskDetail;
import com.prolog.eis.component.algorithm.composeorder.entity.PickingOrderDto;
import com.prolog.eis.component.algorithm.composeorder.entity.StationDto;
import com.prolog.eis.component.algorithm.composeorder.entity.WarehouseDto;
import com.prolog.eis.core.dto.business.outboundtask.OutboundTaskDetailIssueDto;
import com.prolog.eis.core.dto.business.outboundtask.OutboundTaskIssueDto;
import com.prolog.eis.core.model.biz.outbound.OutboundTask;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskBindDetail;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskDetail;
import com.prolog.framework.core.exception.PrologException;
import com.prolog.framework.core.restriction.Criteria;
import com.prolog.framework.core.restriction.Order;
import com.prolog.framework.core.restriction.Restrictions;
import com.prolog.framework.utils.MapUtils;
import com.prolog.framework.utils.StringUtils;

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
    private OutBoundTaskBizService outboundTaskBizService;

    @Autowired
    private PickingOrderService pickingOrderService;

    @Autowired
    private OutboundTaskMapper outboundTaskMapper;
    @Autowired
    private OutboundTaskDetailMapper outboundTaskDetailMapper;
    @Autowired
    private OutboundTaskBindDetailMapper outboundTaskBindDtMapper;

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
        } catch (PrologException ex) {
            log.error("outboundStrategyConfigService.findConfigByTypeNo(B2C) throw excp", ex.toString());
        }
        if (null == config) {
            log.error("outboundStrategyConfigService.findConfigByTypeNo(B2C) return null");
            return;
        }
        String outModel = config.getOutModel();
        String composeOrderConfig = config.getComposeOrderConfig();
        if (OutboundStrategyConfigConstant.OUT_MODEL_PICKING.equals(outModel)) {
            // 出单任务，从订单池获取biz_eis_out_task表 state=未开始
            List<BizOutTask> outTaskList = this.findAllNoStartTask();

            log.info("outboundTaskService.findAllNoStartTask() return:{}", JSONObject.toJSONString(outTaskList));

            WarehouseDto warehouse = outboundTaskBizService.getWarehouseByPickingOrderOutModel(config);
            log.info("outboundTaskBizService.getWarehouseByPickingOrderOutModel({}) return:{}", JSONObject.toJSONString(config), JSONObject.toJSONString(warehouse));
            if (OutboundStrategyConfigConstant.ALGORITHM_COMPOSE_SIMILARITY.equals(composeOrderConfig)) {
                warehouse.getStationList().sort(Comparator.comparingInt(StationDto::computeContainerCount));
                for (StationDto station : warehouse.getStationList()) {
                    try {
                        // 筛选出最合适的任务
                        PickingOrderDto pickingOrderDto = ComposeOrderUtils.compose(station, warehouse, outTaskList);
                        log.info("ComposeOrderUtils.compose({},{},{}) return:{}", JSONObject.toJSONString(station), JSONObject.toJSONString(warehouse), JSONObject.toJSONString(outTaskList), JSONObject.toJSONString(pickingOrderDto));
                        if (pickingOrderDto == null) {
                            continue;
                        }
                        station.getPickingOrderList().add(pickingOrderDto);
                        List<String> taskIdList = pickingOrderDto.getOutboundTaskList().stream().map(BizOutTask::getId).collect(Collectors.toList());
                        String id = pickingOrderService.insert(station.getStationId(), taskIdList);
                        pickingOrderDto.setId(id);
                        station.setNeedPickingOrder(pickingOrderDto);
                        ComposeOrderUtils.removeSelectedDingDan(pickingOrderDto, outTaskList);
                    } catch (Exception e) {
                        log.error("站台绑定订单异常   {}", e.getMessage());
                    }
                }
                this.checkStationNeedOutPickingOrder(warehouse);
                containerOutDispatchService.outContainerForPickingOrder(warehouse, config);
            }
        }
    }

    /**
     *  计算站台需要出库的拣选单
     */
    private void checkStationNeedOutPickingOrder(WarehouseDto warehouse) {
        for (StationDto stationDto : warehouse.getStationList()) {
            for (PickingOrderDto pickingOrderDto : stationDto.getPickingOrderList()) {
                if (pickingOrderDto.getIsAllContainerArrive() != 1) {
                    stationDto.setNeedPickingOrder(pickingOrderDto);
                    break;
                }
            }
        }
    }

    @Override
    public List<BizOutTask> findAllNoStartTask() {
        List<OutboundTask> taskList = outboundTaskMapper.findByMap(MapUtils
                .put("state", OutboundTaskConstant.STATE_NOSTART).getMap(), OutboundTask.class);
        return getBizOutTaskListByTaskList(taskList);
    }

    @Override
    public void batchUpdatePickingOrderId(List<String> idList, String pickingOrderId) throws Exception {
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
     *
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
        Criteria outboundTaskDtCrt = Criteria.forClass(OutboundTaskDetail.class);
        outboundTaskDtCrt.setRestriction(Restrictions.in("outTaskId", taskIdList.toArray()));
        List<OutboundTaskDetail> taskDtList = outboundTaskDetailMapper.findByCriteria(outboundTaskDtCrt);
        // task_detail根据outTaskId分组
        Map<String, List<OutboundTaskDetail>> outTaskIdAndTaskDtListMap = taskDtList.stream().collect(Collectors.groupingBy(OutboundTaskDetail::getOutTaskId));

        // 查询关联的task_bind数据
        List<String> taskDetailIdList = taskDtList.stream().map(OutboundTaskDetail::getId).collect(Collectors.toList());
        Criteria outboundTaskBindDtCrt = Criteria.forClass(OutboundTaskBindDetail.class);
        outboundTaskBindDtCrt.setRestriction(Restrictions.in("outTaskDetailId", taskDetailIdList.toArray()));
        List<OutboundTaskBindDetail> taskBindDtList = outboundTaskBindDtMapper.findByCriteria(outboundTaskBindDtCrt);
        // task_bind根据taskDetailId分组
        Map<String, List<OutboundTaskBindDetail>> outTaskDtIdAndTaskBindDtListMap = taskBindDtList.stream().collect(Collectors.groupingBy(OutboundTaskBindDetail::getOutTaskDetailId));

        for (OutboundTask task : taskList) {
            // 根据数据库OutboundTask->生成对应业务对象BizOutTask
            BizOutTask bizTask = new BizOutTask();
            bizTask.setId(task.getId());
            bizTask.setPickOrderId(task.getPickingOrderId());
            bizTask.setPriority(task.getPriority());
            bizTask.setExpiryDate(task.getExpireDate());

            List<BizOutTaskDetail> bizOutTaskDetailList = Lists.newArrayList();
            // 查询task下属task_detail数据
            List<OutboundTaskDetail> taskDtListByOutTaskId = outTaskIdAndTaskDtListMap.get(task.getId());
            for (OutboundTaskDetail taskDt : taskDtListByOutTaskId) {
                // 数据库对象OutboundTaskDetail->生成对应业务对象BizOutTaskDetail
                BizOutTaskDetail bizOutTaskDetail = new BizOutTaskDetail();
                BeanUtils.copyProperties(taskDt, bizOutTaskDetail);
                // 查询task_detail下属binding数据,计算bindingNum
                float bindingNum = 0.F;
                List<OutboundTaskBindDetail> taskBindDtListByOutTaskDtId = outTaskDtIdAndTaskBindDtListMap.get(taskDt.getId());
                if (!CollectionUtils.isEmpty(taskBindDtListByOutTaskDtId)) {
                    for (OutboundTaskBindDetail taskBindDt : taskBindDtListByOutTaskDtId) {
                        bindingNum += taskBindDt.getBindingNum();
                    }
                }
                bizOutTaskDetail.setBindingNum(bindingNum);
                bizOutTaskDetailList.add(bizOutTaskDetail);
            }
            bizTask.setBizOutTaskDetailList(bizOutTaskDetailList);
            bizOutTaskList.add(bizTask);
        }
        return bizOutTaskList;
    }

    @Autowired
    private OutboundTaskReportService outboundTaskReportService;
    @Autowired
    private OutboundTaskHistoryService outboundTaskHistoryService;
    @Autowired
    private OutboundTaskDetailHistoryService outboundTaskDetailHistoryService;

    @Async
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void genOutboundRpAndHis() throws Exception {
        Criteria crtTask = Criteria.forClass(OutboundTask.class);
        List<OutboundTask> taskList = outboundTaskMapper.findByCriteria(crtTask);
        if (CollectionUtils.isEmpty(taskList)) {
            return;
        }
        // 过滤掉PickingOrderId字段为Null或者为""的数据
        List<OutboundTask> filterTaskList = taskList.stream()
                .filter(s -> !StringUtils.isEmpty(s.getPickingOrderId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(filterTaskList)) {
            return;
        }
        // 查询出货单详情数据
        Criteria outboundTaskDtCrt = Criteria.forClass(OutboundTaskDetail.class);
        List<OutboundTaskDetail> taskDtList = outboundTaskDetailMapper.findByCriteria(outboundTaskDtCrt);
        // 按照PickingOrderId分类
        Map<String, List<OutboundTask>> taskListGroupByPickingOrderIdMap = filterTaskList
                .stream().collect(Collectors.groupingBy(OutboundTask::getPickingOrderId));
        for (String pickingOrderId : taskListGroupByPickingOrderIdMap.keySet()) {
            // 根据pickingOrderId， 判断关联的outboundTask完成情况，进行后续操作
            List<OutboundTask> pickingOrderIdTaskList = taskListGroupByPickingOrderIdMap.get(pickingOrderId);
            if (CollectionUtils.isEmpty(pickingOrderIdTaskList)) {
                continue;
            }
            // 某个拣货单下的出单任务全部已完成
            boolean isPickingOrderIdTaskListStateAllFinish = true;
            for (OutboundTask pickingOrderIdTask : pickingOrderIdTaskList) {
                if (pickingOrderIdTask.getState() != OutboundTaskConstant.STATE_FINISH) {
                    isPickingOrderIdTaskListStateAllFinish = false;
                    break;
                }
            }
            // 全部已完成->生成回执->转入历史
            if (isPickingOrderIdTaskListStateAllFinish) {
                log.info("找到已全部完成的outboundTask, 对应拣选单Id:{}, 准备生成回告，历史，并删除原数据", pickingOrderId);
                // PickingOrderIdTaskList转Id集合
                List<String> pickingOrderIdTaskIdList = pickingOrderIdTaskList.stream().map(OutboundTask::getId).collect(Collectors.toList());
                // 生成回执
                outboundTaskReportService.batchConvertAndInsert(pickingOrderIdTaskList);
                // 出货单转历史
                outboundTaskHistoryService.batchConvertAndInsert(pickingOrderIdTaskList);
                // 删除出货单
                outboundTaskMapper.deleteByIds(pickingOrderIdTaskIdList.toArray(), OutboundTask.class);

                // 过滤关联的出货单明细
                List<OutboundTaskDetail> relaTaskDtList = taskDtList.stream().filter(s -> pickingOrderIdTaskIdList.contains(s.getOutTaskId())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(relaTaskDtList)) {
                    // 关联的出货单明细Id
                    List<String> relaTaskDtIdList = relaTaskDtList.stream().map(OutboundTaskDetail::getId).collect(Collectors.toList());
                    // 出货单明细转历史
                    outboundTaskDetailHistoryService.batchConvertAndInsert(relaTaskDtList);
                    // 删除出货单明细
                    outboundTaskDetailMapper.deleteByIds(relaTaskDtIdList.toArray(), OutboundTaskDetail.class);
                }
            } else {
                log.info("找到没有全部完成的outboundTask, 对应拣选单Id:{}, 不执行操作", pickingOrderId);
            }
        }
    }

    @Override
    public List<OutboundTask> getListByUpperSystemTaskId(
            String upperSystemTaskId) {
        if (StringUtils.isEmpty(upperSystemTaskId)) {
            return Lists.newArrayList();
        }
        Criteria criteria = new Criteria(OutboundTask.class);
        criteria.setRestriction(Restrictions
                .and(Restrictions.eq("upperSystemTaskId", upperSystemTaskId)));
        return outboundTaskMapper.findByCriteria(criteria);
    }

    @Override
    public List<BizOutTask> getListByTypeNoListAndStateList(
            List<String> typeNoList, List<Integer> stateList) {
        if (CollectionUtils.isEmpty(typeNoList)) {
            return Lists.newArrayList();
        }
        if (CollectionUtils.isEmpty(stateList)) {
            return Lists.newArrayList();
        }
        Criteria criteria = new Criteria(OutboundTask.class);
        criteria.setRestriction(Restrictions.and(
                Restrictions.in("outboundTaskTypeNo", typeNoList.toArray()),
                Restrictions.in("state", stateList.toArray())));
        criteria.setOrder(Order.newInstance().asc("priority"));
        return getBizOutTaskListByTaskList(outboundTaskMapper.findByCriteria(criteria));
    }

    @Override
    public OutboundTask getOneById(String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        return outboundTaskMapper.findById(id, OutboundTask.class);
    }

    @Override
    public List<OutboundTaskIssueDto> getOutboundTaskListByContainerNoList(
            List<String> containerNoList) {
        if (CollectionUtils.isEmpty(containerNoList)) {
            return Lists.newArrayList();
        }
        // 根据托盘查询 任务绑定明细
        Criteria outboundTaskBindDtCrt = Criteria.forClass(OutboundTaskBindDetail.class);
        outboundTaskBindDtCrt.setRestriction(Restrictions.in("containerNo", containerNoList.toArray()));
        List<OutboundTaskBindDetail> taskBindDtList = outboundTaskBindDtMapper.findByCriteria(outboundTaskBindDtCrt);
        if (CollectionUtils.isEmpty(taskBindDtList)) {
            return Lists.newArrayList();
        }
        // 根据 任务绑定明细.出库任务明细Id查询出库任务明细
        List<String> taskDtIdList = taskBindDtList.stream().filter(e -> !StringUtils.isEmpty(e.getOutTaskDetailId())).map(e -> e.getOutTaskDetailId()).collect(Collectors.toList());
        Criteria outboundTaskDtCrt = Criteria.forClass(OutboundTaskDetail.class);
        outboundTaskDtCrt.setRestriction(Restrictions.in("id", taskDtIdList.toArray()));
        List<OutboundTaskDetail> taskDtList = outboundTaskDetailMapper.findByCriteria(outboundTaskDtCrt);
        if (CollectionUtils.isEmpty(taskDtList)) {
            return Lists.newArrayList();
        }
        // 根据出库任务明细.出库任务Id查询出库任务
        List<String> taskIdList = taskDtList.stream().filter(e -> !StringUtils.isEmpty(e.getOutTaskId())).map(e -> e.getOutTaskId()).collect(Collectors.toList());
        Criteria outboundTaskCrt = Criteria.forClass(OutboundTask.class);
        outboundTaskCrt.setRestriction(Restrictions.in("id", taskIdList.toArray()));
        List<OutboundTask> taskList = outboundTaskMapper.findByCriteria(outboundTaskCrt);
        if (CollectionUtils.isEmpty(taskList)) {
            return Lists.newArrayList();
        }
        // 将出库任务明细 按照 任务Id分组
        Map<String, List<OutboundTaskDetail>> taskIdAndTaskDtListMap = taskDtList.stream().filter(e -> !StringUtils.isEmpty(e.getOutTaskId())).collect(Collectors.groupingBy(e -> e.getOutTaskId()));
        List<OutboundTaskIssueDto> taskDtoList = Lists.newArrayList();
        for (OutboundTask task : taskList) {
            // 出库任务数据库对象->出库任务业务对象
            OutboundTaskIssueDto taskDto = new OutboundTaskIssueDto();
            BeanUtils.copyProperties(task, taskDto);
            // 出库任务明细数据库对象->出库任务明细业务对象
            List<OutboundTaskDetailIssueDto> taskDtDtoList = Lists.newArrayList();
            List<OutboundTaskDetail> subTaskDtList = taskIdAndTaskDtListMap.get(task.getId());
            if (!CollectionUtils.isEmpty(subTaskDtList)) {
                for (OutboundTaskDetail subTaskDt : subTaskDtList) {
                    OutboundTaskDetailIssueDto taskDtDto = new OutboundTaskDetailIssueDto();
                    BeanUtils.copyProperties(subTaskDt, taskDtDto);
                    taskDtDtoList.add(taskDtDto);
                }
            }
            taskDto.setOutboundTaskDetailIssueList(taskDtDtoList);
            taskDtoList.add(taskDto);
        }
        return taskDtoList;
    }

}