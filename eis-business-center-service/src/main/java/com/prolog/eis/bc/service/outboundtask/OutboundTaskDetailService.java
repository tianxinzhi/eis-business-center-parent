package com.prolog.eis.bc.service.outboundtask;

import com.prolog.eis.core.model.biz.outbound.OutboundTaskDetail;

import java.util.List;

/**
 * @Author: txz
 * @Date: 2021/10/13 16:23
 * @Desc:
 */
public interface OutboundTaskDetailService {

    /**
     * 根据任务单id获取明细
     * @param taskId
     * @return
     */
    public List<OutboundTaskDetail> getByOutTaskId(String taskId);

    /**
     * 根据任务单id集合获取明细
     * @param outTaskIdList 任务单id集合
     * @return
     */
    List<OutboundTaskDetail> getByOutTaskIdList(List<String> outTaskIdList);
}
