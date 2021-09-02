package com.prolog.eis.sc.service.container.impl;

import com.alibaba.fastjson.JSONObject;
import com.prolog.eis.core.model.biz.carry.CarryTask;
import com.prolog.eis.core.model.biz.carry.CarryTaskCallback;
import com.prolog.eis.core.model.biz.container.ContainerTask;
import com.prolog.eis.core.model.biz.container.ContainerTaskDetail;
import com.prolog.eis.core.model.biz.container.ContainerTaskReport;
import com.prolog.eis.core.model.ctrl.container.ContainerTaskStrategy;
import com.prolog.eis.fx.component.business.dao.container.ScContainerTaskDetailMapper;
import com.prolog.eis.fx.component.business.dao.container.ScContainerTaskMapper;
import com.prolog.eis.fx.component.business.dao.container.ScContainerTaskReportMapper;
import com.prolog.eis.fx.component.business.dao.container.ScContainerTaskStrategyMapper;
import com.prolog.eis.sc.feign.container.CarryInterfaceFeign;
import com.prolog.eis.sc.service.container.ScContainerTaskService;
import com.prolog.framework.common.message.RestMessage;
import com.prolog.framework.utils.MapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/8/18
 **/
@Service
@Slf4j
public class ScContainerTaskServiceImpl implements ScContainerTaskService {

    @Autowired
    private CarryInterfaceFeign carryInterfaceFeign;
    @Autowired
    private ScContainerTaskMapper containerTaskMapper;
    @Autowired
    private ScContainerTaskDetailMapper containerTaskDetailMapper;
    @Autowired
    private ScContainerTaskStrategyMapper containerTaskStrategyMapper;
    @Autowired
    private ScContainerTaskReportMapper containerTaskReportMapper;

    @Override
    public void doContainerTask() {

        /**
         * 1.先查询出还未下发的任务
         * 2.生成搬运任务
         */
        List<ContainerTaskStrategy> containerTaskStrategyList = containerTaskStrategyMapper.findByMap(new HashMap<>(), ContainerTaskStrategy.class);

        List<ContainerTaskDetail> containerTaskDetailList = containerTaskDetailMapper.findReadIssueTask();
        containerTaskDetailList.stream().forEach(containerTaskDetail -> {
            try {
                ContainerTask containerTask = containerTaskMapper.findById(containerTaskDetail.getContainerTaskId(), ContainerTask.class);
                String typeNo = containerTask.getTypeNo();

                ContainerTaskStrategy containerTaskStrategy = containerTaskStrategyList.stream().filter(p -> p.getContainerTaskTypeNo().equals(typeNo)).findFirst().orElse(null);
                if (containerTaskStrategy == null) {
                    throw new RuntimeException(String.format("容器任务{%s}的任务类型不在配置中", typeNo));
                }
                CarryTask carryInterfaceTask = new CarryTask();
                carryInterfaceTask.setId(String.valueOf(containerTaskDetail.getId()));
                carryInterfaceTask.setContainerNo(containerTaskDetail.getContainerNo());
                carryInterfaceTask.setTaskType(20);
                carryInterfaceTask.setStartLocation(containerTaskDetail.getSourceArea());
                carryInterfaceTask.setStartLocation(containerTaskDetail.getSourceLocation());
                carryInterfaceTask.setEndRegion(containerTaskDetail.getTargetArea());
                carryInterfaceTask.setEndLocation(containerTaskDetail.getTargetLocation());
                carryInterfaceTask.setPriority(containerTask.getPriority() != 0 ? containerTask.getPriority() : containerTaskStrategy.getPriority());
                String json = JSONObject.toJSONString(carryInterfaceTask);
                RestMessage<String> carry = carryInterfaceFeign.createCarry(json);
                if (carry.isSuccess()) {
                    if (containerTask.getStatus() != ContainerTask.STATUS_ING) {
                        containerTask.setStatus(ContainerTask.STATUS_ING);
                        containerTask.setTaskStartTime(new Date());
                        containerTaskMapper.update(containerTask);
                    }
                    containerTaskDetail.setStatus(ContainerTaskDetail.STATUS_ING);
                    containerTaskDetail.setDetailStartTime(new Date());
                    containerTaskDetailMapper.update(containerTaskDetail);
                } else {
                    throw new Exception(String.format("调用生成搬运任务异常,{%s}", carry.getMessage()));
                }
            } catch (Exception ex) {
                log.error("容器任务明细{}，下发异常，reason{}", containerTaskDetail.getContainerNo(), ex.toString());
            }
        });

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void taskFinish() throws Exception {
        /**
         * 1.查询已完成的搬运回告任务的task_id
         * 2.关联已开始的容器任务明细的ID，如果存在则进行修改完成处理
         * 3.查询容器任务表的明细，如果全部已完成，则转到回告表中进行处理
         */
        List<CarryTaskCallback> containerTaskListIds = containerTaskMapper.findUnFinishTasks();
        if (CollectionUtils.isEmpty(containerTaskListIds)) {
            return;
        }
        String ids = StringUtils.join(containerTaskListIds, ',');
        List<ContainerTaskDetail> containerTaskDetailList = containerTaskDetailMapper.findByIdStr(ids);
        List<String> containerTaskDetailListIds = containerTaskDetailList.stream().map(p -> p.getId()).collect(Collectors.toList());
        String containerTaskDetailListIdString = StringUtils.join(containerTaskDetailListIds, ',');
        containerTaskDetailMapper.updateBatchByIds(containerTaskDetailListIdString, new Date());
        //搬运任务回告转历史


        List<String> containerTaskIds = containerTaskDetailList.stream().map(p -> p.getContainerTaskId()).collect(Collectors.toList());
        for (String containerTaskId : containerTaskIds) {
            if (checkTaskFinish(containerTaskId)) {
                //容器任务完成，转历史，并将生成容器任务回告
                finishAndToHistory(containerTaskId);
            }
        }
    }

    /**
     * 设置完成并转历史
     *
     * @param containerTaskId
     */
    private void finishAndToHistory(String containerTaskId) {
        ContainerTask containerTask = containerTaskMapper.findById(containerTaskId, ContainerTask.class);
        containerTask.setStatus(ContainerTask.STATUS_FINISH);
        containerTask.setTaskFinishTime(new Date());
        containerTaskMapper.update(containerTask);
        containerTaskMapper.toHistory(containerTaskId);
        containerTaskMapper.deleteById(containerTaskId, ContainerTask.class);
        containerTaskDetailMapper.toHistory(containerTaskId);
        containerTaskDetailMapper.deleteByMap(MapUtils.put("containerTaskId", containerTaskId).getMap(), ContainerTaskDetail.class);
        //生成容器回告任务
        ContainerTaskReport containerTaskReport = new ContainerTaskReport();
        containerTaskReport.setContainerTaskId(containerTaskId);
        containerTaskReport.setUpperSystemTaskId(containerTask.getUpperSystemTaskId());
        containerTaskReport.setTypeNo(containerTask.getTypeNo());
        containerTaskReport.setCreateTime(new Date());
        containerTaskReportMapper.save(containerTaskReport);
    }

    /**
     * 检查容器任务是否完成
     *
     * @param containerTaskId
     * @return
     */
    private boolean checkTaskFinish(String containerTaskId) {
        List<ContainerTaskDetail> containerTaskDetailList1 = containerTaskDetailMapper.findByMap(MapUtils.put("containerTaskId", containerTaskId).getMap(), ContainerTaskDetail.class);
        List<ContainerTaskDetail> containerTaskDetailList2 = containerTaskDetailMapper.findByMap(MapUtils.put("containerTaskId", containerTaskId).put("status", ContainerTaskDetail.STATUS_FINISH).getMap(), ContainerTaskDetail.class);
        if (containerTaskDetailList1.size() == containerTaskDetailList2.size()) {
            return true;
        }
        return false;
    }
}
