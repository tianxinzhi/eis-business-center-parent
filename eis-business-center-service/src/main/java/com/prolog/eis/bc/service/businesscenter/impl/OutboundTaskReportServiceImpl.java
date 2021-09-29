package com.prolog.eis.bc.service.businesscenter.impl;

import com.prolog.eis.core.model.biz.outbound.OutboundTaskReport;
import com.prolog.eis.bc.dao.OutboundTaskReportMapper;
import com.prolog.eis.bc.facade.dto.businesscenter.OutboundTaskReportDto;
import com.prolog.eis.bc.service.businesscenter.OutboundTaskReportService;
import com.prolog.framework.core.pojo.Page;
import com.prolog.framework.core.restriction.Criteria;
import com.prolog.framework.core.restriction.Restriction;
import com.prolog.framework.core.restriction.Restrictions;
import com.prolog.framework.dao.util.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class OutboundTaskReportServiceImpl implements OutboundTaskReportService {
    @Autowired
    private OutboundTaskReportMapper outboundTaskReportMapper;

    public Page<OutboundTaskReport> getOutboundTaskReportPage(OutboundTaskReportDto dto){
        PageUtils.startPage(dto.getPageNum(), dto.getPageSize());
        Criteria criteria = new Criteria(OutboundTaskReport.class);
        Restriction r1 = null;
        Restriction r2 = null;
        Restriction r3 = null;
        Restriction r4 = null;
        Restriction r5 = null;
        Restriction r6 = null;
        Restriction r7 = null;
        if (!StringUtils.isEmpty(dto.getOutTaskId())) {
            r1 = Restrictions.like("outTaskId", "%" + dto.getOutTaskId() + "%");
        }
        if (!StringUtils.isEmpty(dto.getUpperSystemTaskId())) {
            r2 = Restrictions.like("upperSystemTaskId", "%" + dto.getUpperSystemTaskId() + "%");
        }
        if (!StringUtils.isEmpty(dto.getOutboundTaskTypeNo())) {
            r3 = Restrictions.eq("outboundTaskTypeNo", dto.getOutboundTaskTypeNo());
        }
        if (dto.getCreateTimeFrom() != null) {
            r4 = Restrictions.ge("createTime", dto.getCreateTimeFrom());
        }
        if (dto.getCreateTimeTo() != null) {
            r5 = Restrictions.le("createTime", dto.getCreateTimeTo());
        }
        if (dto.getReportTimeFrom() != null) {
            r6 = Restrictions.ge("reportTime", dto.getReportTimeFrom());
        }
        if (dto.getReportTimeTo() != null) {
            r7 = Restrictions.le("reportTime", dto.getReportTimeTo());
        }
        criteria.setRestriction(Restrictions.and(r1,r2,r3,r4,r5,r6,r7));
        List<OutboundTaskReport> list = outboundTaskReportMapper.findByCriteria(criteria);
        return PageUtils.getPage(list);
    }
}
