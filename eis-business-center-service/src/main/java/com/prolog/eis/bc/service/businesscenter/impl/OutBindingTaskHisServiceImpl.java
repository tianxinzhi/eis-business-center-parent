package com.prolog.eis.bc.service.businesscenter.impl;

import com.prolog.eis.bc.dao.businesscenter.OutBindingTaskHisMapper;
import com.prolog.eis.bc.facade.dto.businesscenter.OutBindingTaskHisDto;
import com.prolog.eis.bc.service.businesscenter.OutBindingTaskHisService;
import com.prolog.framework.core.pojo.Page;
import com.prolog.framework.dao.util.PageUtils;
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
    public Page<OutBindingTaskHisDto> getOutBindingTaskHisPage(OutBindingTaskHisDto dto)
    {
        List<OutBindingTaskHisDto> list = outBindingTaskHisMapper.outboundTaskHisPage(dto);
        return  PageUtils.getPage(list);
    }
}
