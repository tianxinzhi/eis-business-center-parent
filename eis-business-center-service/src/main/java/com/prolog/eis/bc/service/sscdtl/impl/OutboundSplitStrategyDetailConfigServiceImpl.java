package com.prolog.eis.bc.service.sscdtl.impl;

import com.prolog.eis.bc.dao.ssc.OutboundSplitStrategyConfigMapper;
import com.prolog.eis.bc.dao.sscdtl.OutboundSplitStrategyDetailConfigMapper;
import com.prolog.eis.bc.service.sscdtl.OutboundSplitStrategyDetailConfigService;
import com.prolog.eis.core.model.ctrl.outbound.OutboundSplitStrategyConfig;
import com.prolog.eis.core.model.ctrl.outbound.OutboundSplitStrategyDetailConfig;
import com.prolog.framework.core.restriction.Criteria;
import com.prolog.framework.core.restriction.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @Author: txz
 * @Date: 2021/9/23 16:56
 * @Desc: 出库拆单策略明细
 */
@Service
public class OutboundSplitStrategyDetailConfigServiceImpl implements OutboundSplitStrategyDetailConfigService {

    @Autowired
    private OutboundSplitStrategyDetailConfigMapper mapper;

    @Override
    public List<OutboundSplitStrategyDetailConfig> getDtlsByOutSplitStgCfgId(String cfgId) throws Exception{
//        Assert.notNull(cfgId,"出库拆单策略类型id不可为空");
        Criteria criteria = new Criteria(OutboundSplitStrategyDetailConfig.class);
        criteria.setRestriction(Restrictions.eq("outSplitStgCfgId",cfgId));
        List<OutboundSplitStrategyDetailConfig> strategyDetails = mapper.findByCriteria(criteria);
        Assert.notEmpty(strategyDetails,"未查询到出库拆单策略类型明细: "+cfgId);
        return strategyDetails;
    }
}
