package com.prolog.eis.sc.dto.container;

import lombok.Data;

import java.util.List;

/**
 * @Describe  容器任务下发对外暴露实体
 * @Author clarence_she
 * @Date 2021/8/18
 **/
@Data
public class ContainerTaskIssueDto {

    /**
     * 上游系统任务单ID
     */
    private String upperSystemTaskId;

    /**
     * 容器任务单类型编号
     */
    private String typeNo;

    /**
     * 优先级（如果不存在可为null）
     */
    private Integer priority;

    /**
     * 容器任务明细集合
     */
    private List<ContainerTaskDetailIssueDto> containerTaskDetailIssueDtoList;


}
