package com.prolog.eis.bc.service.businesscenter.impl;

import com.prolog.eis.bc.dao.businesscenter.OutBindingTaskMapper;
import com.prolog.eis.bc.facade.dto.businesscenter.OutBindingTaskDto;
import com.prolog.eis.bc.service.businesscenter.OutBindingTaskService;
import com.prolog.framework.core.pojo.Page;
import com.prolog.framework.dao.util.PageUtils;
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
    @Override
    public Page<OutBindingTaskDto> getOuttaskHisPage(OutBindingTaskDto dto){
        List<OutBindingTaskDto> list = outBindingTaskMapper.outboundTaskDetailDtoPage(dto);
        return  PageUtils.getPage(list);
    }


}
