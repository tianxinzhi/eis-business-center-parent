package com.prolog.eis.bc.service.businesscenter;

import com.prolog.eis.core.model.biz.outbound.OutboundTask;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskReport;

import java.util.List;

import com.prolog.eis.bc.facade.dto.businesscenter.OutboundTaskReportDto;
import com.prolog.framework.core.pojo.Page;

public interface OutboundTaskReportService {
    Page<OutboundTaskReport> getOutboundTaskReportPage(OutboundTaskReportDto dto);

    /**
     * 出货任务->回告->入库
     * @param outboundTaskList 出货任务集合
     * @return
     */
    void batchConvertAndInsert(List<OutboundTask> outboundTaskList)
            throws Exception;

    /**
     * 根据上游系统任务单Id查询出库任务单回告
     * @param upperSystemTaskId 上游系统任务单Id
     * @return
     */
    List<OutboundTaskReport> getListByUpperSystemTaskId(
            String upperSystemTaskId);

}
