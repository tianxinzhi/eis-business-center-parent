package com.prolog.eis.bc.service.businesscenter.impl;

import com.prolog.eis.core.model.biz.outbound.OutboundTask;
import com.prolog.eis.bc.dao.businesscenter.OutTaskManagerDefineMapper;
import com.prolog.eis.bc.facade.dto.businesscenter.OutboundTaskDto;
import com.prolog.eis.bc.service.businesscenter.OutTaskManagerService;
import com.prolog.framework.core.pojo.Page;
import com.prolog.framework.core.restriction.Criteria;
import com.prolog.framework.core.restriction.FieldSelector;
import com.prolog.framework.core.restriction.Restriction;
import com.prolog.framework.core.restriction.Restrictions;
import com.prolog.framework.dao.util.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * OutTaskManagerServiceImpl  出库任务单管理
 * @author ax
 * @since 2021-08-30
 */
@Service
public class OutTaskManagerServiceImpl implements OutTaskManagerService {
    @Autowired
    private OutTaskManagerDefineMapper outTaskManagerDefineMapper;
    /**
     * 分页 + 模糊
     *
     * @param
     * @return
     */
    @Override
    public Page<OutboundTaskDto> getOuttaskPage(OutboundTaskDto dto){
        List<OutboundTaskDto> list = outTaskManagerDefineMapper.outboundTaskDetailDtoPage(dto);
        return  PageUtils.getPage(list);
    }

    /*
     * 修改优先级
     * */
    @Override
    public void updatePriority(String id,int priority) {
        FieldSelector field = FieldSelector.newInstance().include(
                new String[] {"priority"});

        OutboundTask outboundTask = (OutboundTask)outTaskManagerDefineMapper.findById(id, OutboundTask.class);
        if(outboundTask == null) {
            throw new RuntimeException("当前出库任务单ID： " + id+ "没有查询到内容");
        }
        Criteria criteria = Criteria.forClass(OutboundTask.class);
        Restriction r1 = Restrictions.eq("id",id);
        criteria.setRestriction(r1);
        outboundTask.setPriority(priority);
        outTaskManagerDefineMapper.updateFieldsByCriteria(outboundTask, field, criteria);
    }
}
