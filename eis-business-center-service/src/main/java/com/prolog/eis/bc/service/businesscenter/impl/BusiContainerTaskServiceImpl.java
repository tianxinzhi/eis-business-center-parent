package com.prolog.eis.bc.service.businesscenter.impl;

import com.prolog.eis.bc.dao.BizContainerTaskDetailMapper;
import com.prolog.eis.bc.dao.BusiContainerTaskMapper;
import com.prolog.eis.core.model.base.area.WhSubArea;
import com.prolog.eis.core.model.biz.container.ContainerTask;
import com.prolog.eis.bc.facade.dto.businesscenter.BusiContainerTaskDto;
import com.prolog.eis.bc.service.businesscenter.BusiContainerTaskService;
import com.prolog.eis.core.model.biz.container.ContainerTaskDetail;
import com.prolog.framework.core.pojo.Page;
import com.prolog.framework.core.restriction.Criteria;
import com.prolog.framework.core.restriction.FieldSelector;
import com.prolog.framework.core.restriction.Restriction;
import com.prolog.framework.core.restriction.Restrictions;
import com.prolog.framework.dao.util.PageUtils;
import com.prolog.framework.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * BusiContainerTaskServiceImpl  容器任务单管理
 * @author ax
 * @since 2021-09-01
 */
@Service
public class BusiContainerTaskServiceImpl implements BusiContainerTaskService {
    @Autowired
    private BusiContainerTaskMapper containerTaskMapper;

    @Autowired
    private BizContainerTaskDetailMapper bizContainerTaskDetailMapper;

    /*
     * 获取容器任务单
     * */
    @Override
    public Page<BusiContainerTaskDto> getBusiContainerTask(BusiContainerTaskDto dto)
    {
        List<BusiContainerTaskDto> list = containerTaskMapper.getBusiContainerTaskPage(dto);
        return  PageUtils.getPage(list);
    }

    /*
     * 修改优先级
     * */
    @Override
    public void updatePriority(String id,int priority) {
        FieldSelector field = FieldSelector.newInstance().include(
                new String[] {"priority"});

        ContainerTask containerTask = (ContainerTask)containerTaskMapper.findById(id,ContainerTask.class);
        if(containerTask == null) {
            throw new RuntimeException("当前容器任务单ID： " + id+ "没有查询到内容");
        }
        Criteria criteria = Criteria.forClass(ContainerTask.class);
        Restriction r1 = Restrictions.eq("id",id);
        criteria.setRestriction(r1);
        containerTask.setPriority(priority);
        containerTaskMapper.updateFieldsByCriteria(containerTask, field, criteria);
    }

    @Override
    public Page<ContainerTaskDetail> findDetailById(String id, String containerNo, int pageNum, int pageSize) {
        PageUtils.startPage(pageNum,pageSize);

        Criteria criteria = Criteria.forClass(ContainerTaskDetail.class);
        if(StringUtils.isBlank(id)){
            throw new RuntimeException("汇总ID不能为空");
        }
        if(StringUtils.isBlank(containerNo)){
            containerNo = "";
        }
        Restriction r1 = Restrictions.likeAll("containerTaskId",id);
        Restriction r2 = Restrictions.likeAll("containerNo",containerNo);
        criteria.setRestriction(Restrictions.and(r1,r2));
        List<ContainerTaskDetail> byCriteria = bizContainerTaskDetailMapper.findByCriteria(criteria);

        return PageUtils.getPage(byCriteria);
    }

}
