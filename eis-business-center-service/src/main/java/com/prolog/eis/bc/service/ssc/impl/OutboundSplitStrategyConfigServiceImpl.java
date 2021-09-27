package com.prolog.eis.bc.service.ssc.impl;

import com.prolog.eis.bc.dao.ssc.OutboundSplitStrategyConfigMapper;
import com.prolog.eis.bc.service.ssc.OutboundSplitStrategyConfigService;
import com.prolog.eis.core.model.ctrl.outbound.OutboundSplitStrategyConfig;
import com.prolog.framework.core.restriction.Criteria;
import com.prolog.framework.core.restriction.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @Author: txz
 * @Date: 2021/9/23 16:46
 * @Desc: 出库拆单策略service
 */
@Service
public class OutboundSplitStrategyConfigServiceImpl implements OutboundSplitStrategyConfigService {

    @Autowired
    private OutboundSplitStrategyConfigMapper mapper;

    @Override
    public OutboundSplitStrategyConfig getByStrategyTypeNo(String strategyTypeNo) throws Exception{
//        Assert.notNull(strategyTypeNo,"出库拆单策略类型编号不可为空");
        Criteria criteria = new Criteria(OutboundSplitStrategyConfig.class);
        criteria.setRestriction(Restrictions.eq("strategyTypeNo",strategyTypeNo));
        List<OutboundSplitStrategyConfig> strategies = mapper.findByCriteria(criteria);
        Assert.notEmpty(strategies,"未查询到出库拆单策略类型编号: "+strategyTypeNo);
        return strategies.get(0);
    }
}
