package com.prolog.eis.bc.service.outboundtask.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.prolog.eis.bc.dao.OutboundTaskHisMapper;
import com.prolog.eis.bc.service.outboundtask.OutboundTaskHistoryService;
import com.prolog.eis.core.model.biz.outbound.OutboundTask;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskHis;

@Service
public class OutboundTaskHistoryServiceImpl
        implements OutboundTaskHistoryService {

    @Autowired
    private OutboundTaskHisMapper outboundTaskHisMapper;

    @Override
    public void batchConvertAndInsert(List<OutboundTask> outboundTaskList)
            throws Exception {
        if (CollectionUtils.isEmpty(outboundTaskList)) {
            return;
        }
        List<OutboundTaskHis> insertObjList = Lists.newArrayList();
        for (OutboundTask outboundTask : outboundTaskList) {
            OutboundTaskHis insertObj = new OutboundTaskHis();
            BeanUtils.copyProperties(outboundTask, insertObj);
            insertObj.setCreateTime(new Date());
            insertObjList.add(insertObj);
        }
        outboundTaskHisMapper.saveBatch(insertObjList);
    }

}
