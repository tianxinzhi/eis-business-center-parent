package com.prolog.eis.bc.service.container;

/**
 * @Author clarence_she
 * @Date 2021/8/18
 **/
public interface ScContainerTaskService {

    /**
     * 为容器任务生成搬运任务
     */
    void doContainerTask();

    /**
     * 出库任务完成转历史
     */
    void taskFinish()throws Exception;
}
