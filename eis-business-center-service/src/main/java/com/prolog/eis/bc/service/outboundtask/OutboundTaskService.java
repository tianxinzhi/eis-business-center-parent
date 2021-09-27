package com.prolog.eis.bc.service.outboundtask;

import java.util.List;

import com.prolog.eis.component.algorithm.composeorder.entity.BizOutTask;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/9/13
 **/
public interface OutboundTaskService {
    /**
     * 生成拣选单
     */
    void composeAndGenerateOutbound();

    /**
     * 查询所有未开始任务(包含任务详情)
     * @return 任务集合
     */
    List<BizOutTask> findAllNoStartTask();

    /**
     * 批量修改拣选单Id字段
     * @param idList 主键Id集合
     * @param pickingOrderId 拣选单Id
     */
    void batchUpdatePickingOrderId(List<String> idList, String pickingOrderId)
            throws Exception;

    /**
     * 根据拣选单Id集合查询关联OutboundTask
     * @param pickingOrderIdList 拣选单Id集合
     * @return
     */
    List<BizOutTask> findByPickingOrderIdList(List<String> pickingOrderIdList);

}
