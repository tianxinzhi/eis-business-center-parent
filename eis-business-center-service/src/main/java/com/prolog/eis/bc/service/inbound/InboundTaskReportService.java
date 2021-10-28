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
     * 查询所有入库任务回告
     * @return
     */
    List<InboundTaskReport> findAll();

    /**
     * 入库任务回告转历史
     * @param dto 数据对象 id属性必填
     */
    void toCallbackHis(InboundTaskReport dto) throws Exception;

    /**
     * 入库任务回告失败
     * @param dto 数据对象 id属性必填
     */
    void toCallbackFail(InboundTaskReport dto) throws Exception;


}
