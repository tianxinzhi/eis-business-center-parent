package com.prolog.eis.sc.dto.container;

import lombok.Data;


/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/8/18
 **/
@Data
public class ContainerTaskDetailIssueDto {

    /**
     * 上游系统任务明细ID
     */
    private String upperSystemTaskDeailId;

    /**
     * 容器号
     */
    private String containerNo;

    /**
     * 源区域
     */
    private String sourceArea;

    /**
     * 源位置
     */
    private String sourceLocation;

    /**
     * 目标区域
     */
    private String targetArea;

    /**
     * 目标位置
     */
    private String targetLocation;

}
