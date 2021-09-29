package com.prolog.eis.bc.service.businesscenter.impl;

import com.prolog.eis.bc.dao.businesscenter.BusiContainerTaskHisMapper;
import com.prolog.eis.bc.facade.dto.businesscenter.BusiContainerTaskHisDto;
import com.prolog.eis.bc.service.businesscenter.BusiContainerTaskHisService;
import com.prolog.framework.core.pojo.Page;
import com.prolog.framework.dao.util.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * BusiContainerTaskServiceImpl  容器任务单管理
 * @author ax
 * @since 2021-09-01
 */
@Service
public class BusiContainerTaskHisServiceImpl implements BusiContainerTaskHisService {
    @Autowired
    private BusiContainerTaskHisMapper containerTaskHisMapper;
    public Page<BusiContainerTaskHisDto> getBusiContainerTask(BusiContainerTaskHisDto dto)
    {
        List<BusiContainerTaskHisDto> list = containerTaskHisMapper.getBusiContainerTaskHisPage(dto);
        return  PageUtils.getPage(list);
    }
}
