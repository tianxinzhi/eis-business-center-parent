package com.prolog.eis.bc.service.outboundtask;

import java.util.List;

import com.prolog.eis.component.algorithm.composeorder.entity.BizOutTask;
import com.prolog.eis.core.dto.business.outboundtask.OutboundTaskIssueDto;
import com.prolog.eis.core.model.biz.outbound.OutboundTask;

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

    /**
     * 生成拣选单回告和历史
     */
    void genOutboundRpAndHis() throws Exception;

    /**
     * 根据上游系统任务单Id查询
     * @param upperSystemTaskId 上游系统任务单Id
     * @return
     */
    List<OutboundTask> getListByUpperSystemTaskId(String upperSystemTaskId);

    /**
     * 根据typeNo和state条件查询
     * @param typeNoList 类型编号List
     * @param stateList  状态List
     * @return
     */
    List<BizOutTask> getListByTypeNoListAndStateList(List<String> typeNoList,
            List<Integer> stateList);

    /**
     * 根据Id查询
     * @param id 主键Id
     * @return
     */
    OutboundTask getOneById(String id);

    /**
     * 根据容器No查询关联的出库任务
     * @param containerNos 容器No多个以英文,分隔
     * @return
     */
    List<OutboundTaskIssueDto> getOutboundTaskListByContainerNos(
            String containerNos);
}
