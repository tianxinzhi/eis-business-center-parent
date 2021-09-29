package com.prolog.eis.bc.service.businesscenter.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prolog.eis.bc.dao.PickingOrderHisMapper;
import com.prolog.eis.bc.facade.dto.businesscenter.PickingOrderDto2;
import com.prolog.eis.bc.service.businesscenter.PickingOrderHisService;
import com.prolog.eis.core.model.biz.outbound.PickingOrderHis;
import com.prolog.framework.core.pojo.Page;
import com.prolog.framework.core.restriction.Criteria;
import com.prolog.framework.core.restriction.Restriction;
import com.prolog.framework.core.restriction.Restrictions;
import com.prolog.framework.dao.util.PageUtils;

/**
 * PickingOrderHisServiceImpl  拣选单历史管理
 * @author ax
 * @since 2021-09-02
 */
@Service
public class PickingOrderHisServiceImpl implements PickingOrderHisService {
    @Autowired
    private PickingOrderHisMapper pickingOrderHisMapper;

    public Page<PickingOrderHis> getPickingOrderHisPage(PickingOrderDto2 dto){
        PageUtils.startPage(dto.getPageNum(), dto.getPageSize());
        Criteria criteria = new Criteria(PickingOrderHis.class);
        Restriction r1 = null;
        Restriction r2 = null;

        if (dto.getCreateTimeFrom() != null) {
            r1 = Restrictions.ge("createTime", dto.getCreateTimeFrom());
        }
        if (dto.getCreateTimeTo() != null) {
            r2 = Restrictions.le("createTime", dto.getCreateTimeTo());
        }

        criteria.setRestriction(Restrictions.and(r1,r2));

        List<PickingOrderHis> list = pickingOrderHisMapper.findByCriteria(criteria);
        return PageUtils.getPage(list);
    }

}
