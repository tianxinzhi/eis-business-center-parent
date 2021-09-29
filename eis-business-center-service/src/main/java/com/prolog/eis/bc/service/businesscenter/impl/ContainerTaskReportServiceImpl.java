package com.prolog.eis.bc.service.businesscenter.impl;

import com.prolog.eis.bc.dao.businesscenter.ContainerTaskReportMapper;
import com.prolog.eis.bc.service.businesscenter.ContainerTaskReportService;
import com.prolog.eis.core.model.biz.container.ContainerTaskReport;
import com.prolog.eis.bc.facade.dto.businesscenter.ContainerTaskReportDto;
import com.prolog.framework.core.pojo.Page;
import com.prolog.framework.core.restriction.Criteria;
import com.prolog.framework.core.restriction.Restriction;
import com.prolog.framework.core.restriction.Restrictions;
import com.prolog.framework.dao.util.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ContainerTaskReportServiceImpl  容器任务单回告
 * @author ax
 * @since 2021-09-02
 */
@Service
public class ContainerTaskReportServiceImpl   implements ContainerTaskReportService {
    @Autowired
    private ContainerTaskReportMapper containerTaskReportMapper;
    public Page<ContainerTaskReport> getContainerTaskReportPage(ContainerTaskReportDto dto){
        PageUtils.startPage(dto.getPageNum(), dto.getPageSize());
        Criteria criteria = new Criteria(ContainerTaskReport.class);
        Restriction r1 = null;
        Restriction r2 = null;
        Restriction r3 = null;
        Restriction r4 = null;
        Restriction r5 = null;
        if (dto.getContainerTaskId() != null) {
            r1 = Restrictions.eq("containerTaskId", dto.getContainerTaskId());
        }
        if (dto.getTypeNo() != null) {
            r2 = Restrictions.eq("typeNo", dto.getTypeNo());
        }
        if (dto.getUpperSystemTaskId() != null) {
            r3 = Restrictions.eq("upperSystemTaskId", dto.getUpperSystemTaskId());
        }
        if (dto.getCreateTimeFrom() != null) {
            r4 = Restrictions.ge("createTime", dto.getCreateTimeFrom());
        }
        if (dto.getCreateTimeTo() != null) {
            r5 = Restrictions.le("createTime", dto.getCreateTimeTo());
        }

        criteria.setRestriction(Restrictions.and(r1,r2,r3,r4,r5));

        List<ContainerTaskReport> list = containerTaskReportMapper.findByCriteria(criteria);
        return PageUtils.getPage(list);
    }
}
