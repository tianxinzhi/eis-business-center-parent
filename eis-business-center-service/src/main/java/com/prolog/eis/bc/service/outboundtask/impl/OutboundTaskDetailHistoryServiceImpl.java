package com.prolog.eis.bc.service.outboundtask.impl;

import java.util.Date;
import java.util.List;

import com.prolog.eis.bc.service.outboundtask.OutboundTaskDetailHistoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.prolog.eis.bc.dao.OutboundTaskDetailHisMapper;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskDetail;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskDetailHis;

@Service
public class OutboundTaskDetailHistoryServiceImpl implements OutboundTaskDetailHistoryService {

    @Autowired
    private OutboundTaskDetailHisMapper outboundTaskDetailHisMapper;

    @Override
    public void batchConvertAndInsert(
            List<OutboundTaskDetail> outboundTaskDetailList) throws Exception {
        if (CollectionUtils.isEmpty(outboundTaskDetailList)) {
            return;
        }
        List<OutboundTaskDetailHis> insertObjList = Lists.newArrayList();
        for (OutboundTaskDetail outboundTaskDetail : outboundTaskDetailList) {
            OutboundTaskDetailHis insertObj = new OutboundTaskDetailHis();
            BeanUtils.copyProperties(outboundTaskDetail, insertObj);
            insertObj.setCreateTime(new Date());
            insertObjList.add(insertObj);
        }
        outboundTaskDetailHisMapper.saveBatch(insertObjList);
    }

}
