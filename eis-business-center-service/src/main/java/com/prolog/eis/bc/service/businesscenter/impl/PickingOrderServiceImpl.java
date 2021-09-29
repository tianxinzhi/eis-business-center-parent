package com.prolog.eis.bc.service.businesscenter.impl;

import com.prolog.eis.core.model.biz.outbound.PickingOrder;
import com.prolog.eis.bc.dao.businesscenter.PickingOrderMapper;
import com.prolog.eis.bc.facade.dto.businesscenter.PickingOrderDto;
import com.prolog.eis.bc.service.businesscenter.PickingOrderService;
import com.prolog.framework.core.pojo.Page;
import com.prolog.framework.core.restriction.Criteria;
import com.prolog.framework.core.restriction.Restriction;
import com.prolog.framework.core.restriction.Restrictions;
import com.prolog.framework.dao.util.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * PickingOrderServiceImpl  拣选单管理
 * @author ax
 * @since 2021-09-02
 */
@Service
public class PickingOrderServiceImpl implements PickingOrderService {
    @Autowired
    private PickingOrderMapper pickingOrderMapper;

    public Page<PickingOrder> getPickingOrderPage(PickingOrderDto dto){
        PageUtils.startPage(dto.getPageNum(), dto.getPageSize());
        Criteria criteria = new Criteria(PickingOrder.class);
        Restriction r1 = null;
        Restriction r2 = null;

        if (dto.getCreateTimeFrom() != null) {
            r1 = Restrictions.ge("createTime", dto.getCreateTimeFrom());
        }
        if (dto.getCreateTimeTo() != null) {
            r2 = Restrictions.le("createTime", dto.getCreateTimeTo());
        }

        criteria.setRestriction(Restrictions.and(r1,r2));

        List<PickingOrder> list = pickingOrderMapper.findByCriteria(criteria);
        return PageUtils.getPage(list);
    }

}
