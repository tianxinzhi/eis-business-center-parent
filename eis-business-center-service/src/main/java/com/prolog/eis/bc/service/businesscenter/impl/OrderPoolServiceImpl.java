package com.prolog.eis.bc.service.businesscenter.impl;

import com.prolog.eis.bc.service.businesscenter.OrderPoolService;
import com.prolog.eis.core.model.biz.outbound.OrderPool;
import com.prolog.eis.bc.dao.OrderPoolMapper;
import com.prolog.eis.bc.facade.dto.businesscenter.OrderPoolDto;
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
 * OrderPoolServiceImpl  实时汇总单查询
 * @author ax
 * @since 2021-09-02
 */
@Service
public class OrderPoolServiceImpl implements OrderPoolService {
    @Autowired
    private OrderPoolMapper orderPoolMapper;
    @Override
    public Page<OrderPool> getOrderPoolPage(OrderPoolDto dto){
        PageUtils.startPage(dto.getPageNum(), dto.getPageSize());
        Criteria criteria = new Criteria(OrderPool.class);
        Restriction r1 = null;
        Restriction r2 = null;
        Restriction r3 = null;
        Restriction r4 = null;
        if (dto.getName() != null) {
            r1 = Restrictions.eq("name", dto.getName());
        }
        if (dto.getTypeNo() != null) {
            r2 = Restrictions.eq("typeNo", dto.getTypeNo());
        }
        if (dto.getCreateTimeFrom() != null) {
            r3 = Restrictions.ge("createTime", dto.getCreateTimeFrom());
        }
        if (dto.getCreateTimeTo() != null) {
            r4 = Restrictions.le("createTime", dto.getCreateTimeTo());
        }

        criteria.setRestriction(Restrictions.and(r1,r2,r3,r4));

        List<OrderPool> list = orderPoolMapper.findByCriteria(criteria);
        return PageUtils.getPage(list);
    }
    @Override
    public long modify(OrderPool orderPool){
        {
            if(StringUtils.isEmpty(orderPool)) {
                throw new RuntimeException("请传入对应的参数");
            }
            if(StringUtils.isEmpty(orderPool.getName())) {
                throw new RuntimeException("请输入订单池批拣单名称");
            }
            if(StringUtils.isEmpty(orderPool.getTypeNo())) {
                throw new RuntimeException("请选择出库任务单类型编号");
            }

//            if(StringUtils.isEmpty(orderPool.getMaxOrderNum())) {
//                throw new RuntimeException("请输入最大汇单数量");
//            }
            return orderPoolMapper.update(orderPool);
        }
    }
    @Override
    public long add(OrderPool orderPool){
        if(StringUtils.isEmpty(orderPool)) {
            throw new RuntimeException("请传入对应的参数");
        }
        if(StringUtils.isEmpty(orderPool.getName())) {
            throw new RuntimeException("请输入订单池批拣单名称");
        }
        if(StringUtils.isEmpty(orderPool.getTypeNo())) {
            throw new RuntimeException("请选择出库任务单类型编号");
        }

//        if(StringUtils.isEmpty(orderPool.getMaxOrderNum())) {
//            throw new RuntimeException("请输入最大汇单数量");
//        }
        return  orderPoolMapper.save(orderPool);
    }
    @Override
    public void deleted(Integer id){
        if(StringUtils.isEmpty(id)) {
            throw new RuntimeException("请选择对应的参数后删除");
        }
        orderPoolMapper.deleteById(id, OrderPool.class);
    }
}
