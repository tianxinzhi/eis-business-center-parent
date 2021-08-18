package com.prolog.eis.sc.service.container.impl;

import com.prolog.eis.sc.service.container.ContainerTaskService;
import com.prolog.eis.sc.dto.container.ContainerTaskIssueDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Describe 容器任务实现
 * @Author clarence_she
 * @Date 2021/8/18
 **/
@Service
public class ContainerTaskServiceImpl implements ContainerTaskService {

    @Override
    public boolean containerTaskIssue(List<ContainerTaskIssueDto> containerTaskIssueDtoList) throws Exception {
        return false;
    }
}
