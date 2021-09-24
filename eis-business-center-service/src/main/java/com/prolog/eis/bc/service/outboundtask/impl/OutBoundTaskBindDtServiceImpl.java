package com.prolog.eis.bc.service.outboundtask.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Maps;
import com.prolog.eis.bc.dao.OutboundTaskBindDtMapper;
import com.prolog.eis.bc.service.outboundtask.OutboundTaskBindDtService;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskBindDetail;

@Service
public class OutBoundTaskBindDtServiceImpl implements OutboundTaskBindDtService {

    @Autowired
    private OutboundTaskBindDtMapper outboundTaskBindDtMapper;

    @Override
    public Map<String, Integer> findSumBindingNumGroupByLotId() {
        List<OutboundTaskBindDetail> list = outboundTaskBindDtMapper.findSumBindingNumGroupByLotId();
        if (CollectionUtils.isEmpty(list)) {
            return Maps.newHashMap();
        }
        return list.stream().collect(Collectors.toMap(OutboundTaskBindDetail::getLotId, p -> (int) p.getBindingNum()));
    }

    @Override
    public Map<String, Integer> findSumBindingNumGroupByItemId() {
        List<OutboundTaskBindDetail> list = outboundTaskBindDtMapper.findSumBindingNumGroupByItemId();
        if (CollectionUtils.isEmpty(list)) {
            return Maps.newHashMap();
        }
        return list.stream().collect(Collectors.toMap(OutboundTaskBindDetail::getItemId, p -> (int) p.getBindingNum()));
    }

}
