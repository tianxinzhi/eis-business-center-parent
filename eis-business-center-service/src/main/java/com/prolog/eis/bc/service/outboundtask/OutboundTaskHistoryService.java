package com.prolog.eis.bc.service.outboundtask;

import java.util.List;

import com.prolog.eis.core.model.biz.outbound.OutboundTask;

/**
 * 出货任务历史Service
 * @author 金总
 *
 */
public interface OutboundTaskHistoryService {

    /**
     * 出货任务->历史->入库
     * @param outboundTaskList 出货任务集合
     * @return
     */
    void batchConvertAndInsert(List<OutboundTask> outboundTaskList) throws Exception;

}
