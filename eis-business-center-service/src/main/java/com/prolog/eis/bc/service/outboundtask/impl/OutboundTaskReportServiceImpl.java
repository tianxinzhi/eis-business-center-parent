package com.prolog.eis.bc.service.outboundtask.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.prolog.eis.bc.dao.OutboundTaskReportMapper;
import com.prolog.eis.bc.service.outboundtask.OutboundTaskReportService;
import com.prolog.eis.core.model.biz.outbound.OutboundTask;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskReport;

@Service
public class OutboundTaskReportServiceImpl
        implements OutboundTaskReportService {

    @Autowired
    private OutboundTaskReportMapper outboundTaskReportMapper;

    @Override
    public void batchConvertAndInsert(List<OutboundTask> outboundTaskList)
            throws Exception {
        if (CollectionUtils.isEmpty(outboundTaskList)) {
            return;
        }
        List<OutboundTaskReport> insertObjList = Lists.newArrayList();
        for (OutboundTask outboundTask : outboundTaskList) {
            OutboundTaskReport insertObj = new OutboundTaskReport();
            insertObj.setOutTaskId(outboundTask.getId());
            insertObj.setOutboundTaskTypeNo(outboundTask.getOutboundTaskTypeNo());
            insertObj.setUpperSystemTaskId(outboundTask.getUpperSystemTaskId());
            insertObj.setCreateTime(new Date());
            insertObjList.add(insertObj);
        }
        outboundTaskReportMapper.saveBatch(insertObjList);
    }

}
