package com.prolog.eis.bc.service.businesscenter.impl;

import com.prolog.eis.bc.dao.OutBindingTaskMapper;
import com.prolog.eis.bc.dao.OutboundTaskBindDetailMapper;
import com.prolog.eis.bc.facade.dto.businesscenter.OutBindingTaskDto;
import com.prolog.eis.bc.service.businesscenter.OutBindingTaskService;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskBindDetail;
import com.prolog.framework.core.pojo.Page;
import com.prolog.framework.dao.util.PageUtils;
import com.prolog.framework.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * OutTaskHisManagerServiceImpl  出库任务单管理
 * @author ax
 * @since 2021-09-01
 */
@Service
public class OutBindingTaskServiceImpl implements OutBindingTaskService {
    @Autowired
    private OutBindingTaskMapper outBindingTaskMapper;
    @Autowired
    private OutboundTaskBindDetailMapper outboundTaskBindDtMapper;

    @Override
    public Page<OutBindingTaskDto> getOuttaskHisPage(OutBindingTaskDto dto){
        List<OutBindingTaskDto> list = outBindingTaskMapper.outboundTaskDetailDtoPage(dto);
        return  PageUtils.getPage(list);
    }

    @Override
    public List<OutboundTaskBindDetail> getOutBindingTaskDetail(String id) {
        List<OutboundTaskBindDetail> byMap = outboundTaskBindDtMapper.findByMap(MapUtils.put("outbTaskBindId", id).getMap(), OutboundTaskBindDetail.class);
        return byMap;
    }


}
