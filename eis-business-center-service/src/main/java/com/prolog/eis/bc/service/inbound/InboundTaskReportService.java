package com.prolog.eis.bc.service.inbound;

import java.util.List;

import com.prolog.eis.bc.facade.dto.inbound.InboundTaskReportDto;
import com.prolog.eis.bc.facade.dto.inbound.InboundTaskReportHisDto;
import com.prolog.eis.bc.facade.vo.inbound.InboundTaskReportHisVo;
import com.prolog.eis.bc.facade.vo.inbound.InboundTaskReportVo;
import com.prolog.eis.core.model.biz.inbound.InboundTask;
import com.prolog.eis.core.model.biz.inbound.InboundTaskReport;
import com.prolog.framework.core.pojo.Page;

/**
 * @author: wuxl
 * @create: 2021-10-18 17:01
 * @Version: V1.0
 */
public interface InboundTaskReportService {

    /**
     * 前端分页查询
     *
     * @param dto
     * @return
     */
    Page<InboundTaskReportVo> listInboundTaskReportByPage(InboundTaskReportDto dto);

    /**
     * 前端分页查询
     *
     * @param dto
     * @return
     */
    Page<InboundTaskReportHisVo> listInboundTaskReportHisByPage(InboundTaskReportHisDto dto);

    /**
     * 转回告
     *
     * @param inboundTask
     */
    void toReport(InboundTask inboundTask);

    /**
     * 根据上游系统任务单Id查询入库任务单回告
     * @param upperSystemTaskId 上游系统任务单Id
     * @return
     */
    List<InboundTaskReport> getListByUpperSystemTaskId(
            String upperSystemTaskId);
}
