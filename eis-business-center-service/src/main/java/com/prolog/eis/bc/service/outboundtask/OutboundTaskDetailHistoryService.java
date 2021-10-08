package com.prolog.eis.bc.service.outboundtask;

import java.util.List;

import com.prolog.eis.core.model.biz.outbound.OutboundTaskDetail;

/**
 * 出货任务明细历史Service
 * @author 金总
 *
 */
public interface OutboundTaskDetailHistoryService {

    /**
     * 出货任务明细->历史->入库
     * @param outboundTaskDetailList 出货任务明细
     * @return
     */
    void batchConvertAndInsert(List<OutboundTaskDetail> outboundTaskDetailList) throws Exception;

}
