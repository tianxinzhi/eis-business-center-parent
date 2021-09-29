package com.prolog.eis.bc.service.businesscenter.impl;

import com.prolog.eis.core.model.biz.outbound.OutboundSummaryOrder;
import com.prolog.eis.bc.dao.businesscenter.OutboundSummaryOrderMapper;
import com.prolog.eis.bc.facade.dto.businesscenter.OutboundSummaryOrderDto;
import com.prolog.eis.bc.service.businesscenter.OutboundSummaryOrderService;
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
 * OutboundSummaryOrderServiceImpl  出库汇总任务单管理
 * @author ax
 * @since 2021-09-02
 */
@Service
public class OutboundSummaryOrderServiceImpl implements OutboundSummaryOrderService {
    @Autowired
    private OutboundSummaryOrderMapper outboundSummaryOrderMapper;
    public Page<OutboundSummaryOrder> getutboundSummaryOrderPage(OutboundSummaryOrderDto dto){
        PageUtils.startPage(dto.getPageNum(), dto.getPageSize());
        Criteria criteria = new Criteria(OutboundSummaryOrder.class);
        Restriction r1 = null;
        Restriction r2 = null;
        Restriction r3 = null;
        Restriction r4 = null;

        if (!StringUtils.isEmpty(dto.getTypeNo())) {
            r1 = Restrictions.eq("outTaskId", dto.getTypeNo());
        }
        if (!StringUtils.isEmpty(dto.getState())) {
            r2 = Restrictions.eq("state", dto.getState());
        }

        if (dto.getCreateTimeFrom() != null) {
            r3 = Restrictions.ge("createTime", dto.getCreateTimeFrom());
        }
        if (dto.getCreateTimeTo() != null) {
            r4 = Restrictions.le("createTime", dto.getCreateTimeTo());
        }

        criteria.setRestriction(Restrictions.and(r1,r2,r3,r4));
        List<OutboundSummaryOrder> list = outboundSummaryOrderMapper.findByCriteria(criteria);
        return PageUtils.getPage(list);
    }
}
