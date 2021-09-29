package com.prolog.eis.bc.service.businesscenter.impl;

import com.prolog.eis.bc.dao.OutBindingTaskHisMapper;
import com.prolog.eis.bc.dao.OutboundTaskBindDetailHisMapper;
import com.prolog.eis.bc.dao.OutboundTaskBindDetailMapper;
import com.prolog.eis.bc.facade.dto.businesscenter.OutBindingTaskHisDto;
import com.prolog.eis.bc.service.businesscenter.OutBindingTaskHisService;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskBindDetail;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskBindDetailHis;
import com.prolog.framework.core.pojo.Page;
import com.prolog.framework.dao.util.PageUtils;
import com.prolog.framework.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * OutBindingTaskHisServiceImpl  出库任务单容器绑定任务历史查询
 * @author ax
 * @since 2021-09-01
 */
@Service
public class OutBindingTaskHisServiceImpl  implements OutBindingTaskHisService {
    @Autowired
    private OutBindingTaskHisMapper outBindingTaskHisMapper;
    @Autowired
    private OutboundTaskBindDetailHisMapper outboundTaskBindDetailHisMapper;

    @Override
    public Page<OutBindingTaskHisDto> getOutBindingTaskHisPage(OutBindingTaskHisDto dto)
    {
        List<OutBindingTaskHisDto> list = outBindingTaskHisMapper.outboundTaskHisPage(dto);
        return  PageUtils.getPage(list);
    }

    @Override
    public List<OutboundTaskBindDetailHis> getOutBindingTaskDetail(String id) {
        return outboundTaskBindDetailHisMapper.findByMap(MapUtils.put("outbTaskBindId",id).getMap(), OutboundTaskBindDetailHis.class);
    }
}
