package com.prolog.eis.bc.service.businesscenter.impl;

import com.prolog.eis.core.model.biz.container.ContainerTaskReportHis;
import com.prolog.eis.bc.dao.ContainerTaskReportHisMapper;
import com.prolog.eis.bc.facade.dto.businesscenter.ContainerTaskReportHisDto;
import com.prolog.eis.bc.service.businesscenter.ContainerTaskReportHisService;
import com.prolog.framework.core.pojo.Page;
import com.prolog.framework.core.restriction.Criteria;
import com.prolog.framework.core.restriction.Restriction;
import com.prolog.framework.core.restriction.Restrictions;
import com.prolog.framework.dao.util.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * ContainerTaskReportHisServiceImpl  容器任务单历史回告
 * @author ax
 * @since 2021-09-02
 */
@Service
public class ContainerTaskReportHisServiceImpl implements ContainerTaskReportHisService {
    @Autowired
    private ContainerTaskReportHisMapper containerTaskReportHisMapper;
    public Page<ContainerTaskReportHis> getContainerTaskReportHisPage(ContainerTaskReportHisDto dto){
        PageUtils.startPage(dto.getPageNum(), dto.getPageSize());
        Criteria criteria = new Criteria(ContainerTaskReportHis.class);
        Restriction r1 = null;
        Restriction r2 = null;
        Restriction r3 = null;
        Restriction r4 = null;
        Restriction r5 = null;
        if (!StringUtils.isEmpty(dto.getContainerTaskId())) {
            r1 = Restrictions.eq("containerTaskId", dto.getContainerTaskId());
        }
        if (!StringUtils.isEmpty(dto.getTypeNo())) {
            r2 = Restrictions.eq("typeNo", dto.getTypeNo());
        }
        if (!StringUtils.isEmpty(dto.getUpperSystemTaskId())) {
            r3 = Restrictions.eq("upperSystemTaskId", dto.getUpperSystemTaskId());
        }
        if (!StringUtils.isEmpty(dto.getCreateTimeFrom())) {
            r4 = Restrictions.ge("createTime", dto.getCreateTimeFrom());
        }
        if (!StringUtils.isEmpty(dto.getCreateTimeTo())) {
            r5 = Restrictions.le("createTime", dto.getCreateTimeTo());
        }

        criteria.setRestriction(Restrictions.and(r1,r2,r3,r4,r5));

        List<ContainerTaskReportHis> list = containerTaskReportHisMapper.findByCriteria(criteria);
        return PageUtils.getPage(list);
    }
}
