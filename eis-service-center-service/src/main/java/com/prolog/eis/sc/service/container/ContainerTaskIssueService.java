package com.prolog.eis.sc.service.container;

import com.prolog.eis.sc.dto.container.ContainerTaskIssueDto;

import java.util.List;

/**
 * @Describe 容器任务接口
 * @Author clarence_she
 * @Date 2021/8/18
 **/
public interface ContainerTaskIssueService {

    /**
     * 容器任务下发
     * @param containerTaskIssueDtoList
     * @return 返回错误的任务集合
     * @throws Exception
     */
    List<ContainerTaskIssueDto> containerTaskIssue(List<ContainerTaskIssueDto> containerTaskIssueDtoList)throws Exception;


}
