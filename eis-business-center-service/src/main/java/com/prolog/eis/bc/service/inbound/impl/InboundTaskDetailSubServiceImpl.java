package com.prolog.eis.bc.service.inbound.impl;

import com.google.common.collect.Maps;
import com.prolog.eis.bc.dao.inbound.InboundTaskDetailSubHisMapper;
import com.prolog.eis.bc.dao.inbound.InboundTaskDetailSubMapper;
import com.prolog.eis.bc.service.inbound.InboundTaskDetailSubService;
import com.prolog.eis.core.model.biz.inbound.InboundTaskDetailSub;
import com.prolog.framework.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author: wuxl
 * @create: 2021-10-18 17:56
 * @Version: V1.0
 */
@Service
@Slf4j
public class InboundTaskDetailSubServiceImpl implements InboundTaskDetailSubService {
    @Autowired
    private InboundTaskDetailSubMapper inboundTaskDetailSubMapper;
    @Autowired
    private InboundTaskDetailSubHisMapper inboundTaskDetailSubHisMapper;

    @Override
    public List<InboundTaskDetailSub> listInboundTaskDetailSubByParam(String inboundTaskDetailId) {
        Map<String, Object> map = Maps.newHashMap();
        if (!StringUtils.isEmpty(inboundTaskDetailId)) {
            map.put("inboundTaskDetailId", inboundTaskDetailId);
        }
        return inboundTaskDetailSubMapper.findByMap(map, InboundTaskDetailSub.class);
    }

    @Override
    public void toHistory(String id) throws Exception {
        if (StringUtils.isEmpty(id)) {
            throw new Exception("ID不能为空！");
        }
        inboundTaskDetailSubHisMapper.toHistory(id);
        inboundTaskDetailSubMapper.deleteById(id, InboundTaskDetailSub.class);
    }

    @Override
    public long saveBatch(List<InboundTaskDetailSub> subList) {
        return inboundTaskDetailSubMapper.saveBatch(subList);
    }
}
