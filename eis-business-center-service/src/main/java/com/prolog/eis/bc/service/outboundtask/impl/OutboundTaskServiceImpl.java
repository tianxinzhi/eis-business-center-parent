package com.prolog.eis.bc.service.outboundtask.impl;

import com.prolog.eis.bc.service.outboundtask.OutboundTaskService;
import org.springframework.stereotype.Service;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/9/13
 **/
@Service
public class OutboundTaskServiceImpl implements OutboundTaskService {

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




    }
}
