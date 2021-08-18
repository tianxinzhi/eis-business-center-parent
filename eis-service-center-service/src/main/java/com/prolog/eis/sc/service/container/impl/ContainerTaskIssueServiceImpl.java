package com.prolog.eis.sc.service.container.impl;

import com.prolog.eis.model.sc.containertask.ContainerTask;
import com.prolog.eis.model.sc.containertask.ContainerTaskDetail;
import com.prolog.eis.sc.dao.container.ScContainerTaskDetailMapper;
import com.prolog.eis.sc.dao.container.ScContainerTaskMapper;
import com.prolog.eis.sc.dto.container.ContainerTaskDetailIssueDto;
import com.prolog.eis.sc.service.container.ContainerTaskIssueService;
import com.prolog.eis.sc.dto.container.ContainerTaskIssueDto;
import com.prolog.eis.util.PrologDateUtils;
import com.prolog.framework.core.annotation.AutoKey;
import com.prolog.framework.utils.MapUtils;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Describe 容器任务实现
 * @Author clarence_she
 * @Date 2021/8/18
 **/
@Service
@Slf4j
public class ContainerTaskIssueServiceImpl implements ContainerTaskIssueService {

    @Autowired
    private ScContainerTaskMapper containerTaskMapper;
    @Autowired
    private ScContainerTaskDetailMapper containerTaskDetailMapper;

    @Override
    @Transactional
    public List<ContainerTaskIssueDto> containerTaskIssue(List<ContainerTaskIssueDto> containerTaskIssueDtoList) throws Exception {
        List<ContainerTaskIssueDto> error = new ArrayList<>();
        containerTaskIssueDtoList.stream().forEach(
                containerTaskIssueDto -> {
                    try {
                        ContainerTask containerTask = saveTask(containerTaskIssueDto);
                        List<ContainerTaskDetailIssueDto> containerTaskDetailIssueDtoList = containerTaskIssueDto.getContainerTaskDetailIssueDtoList();
                        saveDetail(containerTaskDetailIssueDtoList,containerTask);
                    }catch (Exception ex){
                        log.error("containerTaskIssue error reason {}",ex.toString());
                        error.add(containerTaskIssueDto);
                    }
                }
        );
        return error;
    }

    /**
     * 保存汇总
     * @param containerTaskIssueDto
     */
    private ContainerTask saveTask(ContainerTaskIssueDto containerTaskIssueDto){
        List<ContainerTask> containerTaskList = containerTaskMapper.findByMap(MapUtils.put("upperSystemTaskId", containerTaskIssueDto.getUpperSystemTaskId()).getMap(), ContainerTask.class);
        Assert.isNull(containerTaskList,String.format("任务ID[%s]已存在",containerTaskIssueDto.getUpperSystemTaskId()));
        ContainerTask containerTask = new ContainerTask();
        containerTask.setUpperSystemTaskId(containerTaskIssueDto.getUpperSystemTaskId());
        Assert.notNull(containerTaskIssueDto.getTypeNo(),"任务类型不能为空");
        containerTask.setTypeNo(containerTaskIssueDto.getTypeNo());
        containerTask.setPriority(containerTaskIssueDto.getPriority());
        containerTask.setCreateTime(new Date());
        containerTask.setStatus(ContainerTask.STATUS_CREATE);
        containerTaskMapper.save(containerTask);
        return containerTask;
    }

    /**
     * 保存明细
     * @param containerTaskDetailIssueDtoList
     * @param containerTask
     */
    private void saveDetail(@NotNull List<ContainerTaskDetailIssueDto> containerTaskDetailIssueDtoList, @NotNull ContainerTask containerTask){
        List<ContainerTaskDetail> containerTaskDetailList = new ArrayList<>();
        containerTaskDetailIssueDtoList.stream().forEach(
                containerTaskDetailIssueDto -> {
                    ContainerTaskDetail containerTaskDetail = new ContainerTaskDetail();
                    containerTaskDetail.setContainerTaskId(containerTask.getId());
                    containerTaskDetail.setContainerNo(containerTaskDetailIssueDto.getContainerNo());
                    containerTaskDetail.setStatus(ContainerTaskDetail.STATUS_CREATE);
                    containerTaskDetail.setUpperSystemTaskDeailId(containerTaskDetailIssueDto.getUpperSystemTaskDeailId());
                    containerTaskDetail.setSourceArea(containerTaskDetailIssueDto.getSourceArea());
                    containerTaskDetail.setSourceLocation(containerTaskDetailIssueDto.getSourceLocation());
                    containerTaskDetail.setTargetArea(containerTaskDetailIssueDto.getTargetArea());
                    containerTaskDetail.setTargetLocation(containerTaskDetailIssueDto.getTargetLocation());
                    containerTaskDetail.setCreateTime(new Date());
                    containerTaskDetailList.add(containerTaskDetail);
                }
        );
        containerTaskDetailMapper.saveBatch(containerTaskDetailList);
    }
}
