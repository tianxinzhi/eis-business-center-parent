package com.prolog.eis.bc.service.inbound.impl;

import com.prolog.eis.bc.dao.inbound.InboundTaskReportHisMapper;
import com.prolog.eis.bc.dao.inbound.InboundTaskReportMapper;
import com.prolog.eis.bc.facade.dto.inbound.InboundTaskReportDto;
import com.prolog.eis.bc.facade.dto.inbound.InboundTaskReportHisDto;
import com.prolog.eis.bc.facade.vo.inbound.InboundTaskReportHisVo;
import com.prolog.eis.bc.facade.vo.inbound.InboundTaskReportVo;
import com.prolog.eis.bc.service.inbound.InboundTaskReportService;
import com.prolog.eis.core.model.biz.inbound.InboundTask;
import com.prolog.eis.core.model.biz.inbound.InboundTaskReport;
import com.prolog.framework.core.pojo.Page;
import com.prolog.framework.dao.util.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author: wuxl
 * @create: 2021-10-20 15:04
 * @Version: V1.0
 */
@Service
@Slf4j
public class InboundTaskReportServiceImpl implements InboundTaskReportService {
    @Autowired
    private InboundTaskReportMapper inboundTaskReportMapper;
    @Autowired
    private InboundTaskReportHisMapper inboundTaskReportHisMapper;

    @Override
    public Page<InboundTaskReportVo> listInboundTaskReportByPage(InboundTaskReportDto dto) {
        PageUtils.startPage(dto.getPageNum(), dto.getPageSize());
        List<InboundTaskReportVo> inboundTaskReportVoList = inboundTaskReportMapper.findByParam(dto);
        return PageUtils.getPage(inboundTaskReportVoList);
    }

    @Override
    public Page<InboundTaskReportHisVo> listInboundTaskReportHisByPage(InboundTaskReportHisDto dto) {
        PageUtils.startPage(dto.getPageNum(), dto.getPageSize());
        List<InboundTaskReportHisVo> inboundTaskReportHisVoList = inboundTaskReportHisMapper.findByParam(dto);
        return PageUtils.getPage(inboundTaskReportHisVoList);
    }

    @Override
    public void toReport(InboundTask inboundTask) {
        InboundTaskReport taskReport = new InboundTaskReport();
        taskReport.setInboundTaskId(inboundTask.getId());
        taskReport.setUpperSystemTaskId(inboundTask.getUpperSystemTaskId());
        taskReport.setCreateTime(new Date());

        inboundTaskReportMapper.save(taskReport);
    }
}
