package com.prolog.eis.bc.service.businesscenter.impl;

import com.prolog.eis.bc.dao.BusiContainerTaskDetailHisMapper;
import com.prolog.eis.bc.dao.BusiContainerTaskHisMapper;
import com.prolog.eis.bc.facade.dto.businesscenter.BusiContainerTaskHisDto;
import com.prolog.eis.bc.service.businesscenter.BusiContainerTaskHisService;
import com.prolog.eis.core.model.biz.container.ContainerTaskDetail;
import com.prolog.eis.core.model.biz.container.ContainerTaskDetailHis;
import com.prolog.framework.core.pojo.Page;
import com.prolog.framework.core.restriction.Criteria;
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
public class BusiContainerTaskHisServiceImpl implements BusiContainerTaskHisService {
    @Autowired
    private BusiContainerTaskHisMapper containerTaskHisMapper;
    @Autowired
    private BusiContainerTaskDetailHisMapper busiContainerTaskDetailHisMapper;

    @Override
    public Page<BusiContainerTaskHisDto> getBusiContainerTask(BusiContainerTaskHisDto dto)
    {
        List<BusiContainerTaskHisDto> list = containerTaskHisMapper.getBusiContainerTaskHisPage(dto);
        return  PageUtils.getPage(list);
    }

    @Override
    public Page<ContainerTaskDetailHis> findDetailById(String id, String containerNo, int pageNum, int pageSize) {
        PageUtils.startPage(pageNum,pageSize);

        Criteria criteria = Criteria.forClass(ContainerTaskDetailHis.class);
        if(StringUtils.isBlank(id)){
            throw new RuntimeException("汇总ID不能为空");
        }
        if(StringUtils.isBlank(containerNo)){
            containerNo = "";
        }
        Restriction r1 = Restrictions.likeAll("containerTaskId",id);
        Restriction r2 = Restrictions.likeAll("containerNo",containerNo);
        criteria.setRestriction(Restrictions.and(r1,r2));
        List<ContainerTaskDetailHis> byCriteria = busiContainerTaskDetailHisMapper.findByCriteria(criteria);

        return PageUtils.getPage(byCriteria);
    }
}
