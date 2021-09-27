package com.prolog.eis.bc.service.sscdtl;

import com.prolog.eis.core.model.ctrl.outbound.OutboundSplitStrategyDetailConfig;

import java.util.List;

/**
 * @Author: txz
 * @Date: 2021/9/23 16:53
 */
public interface OutboundSplitStrategyDetailConfigService {

    /**
     * 根据出库拆单策略id获取拆单策略明细
     * @param cfgId
     * @return
     */

    List<OutboundSplitStrategyDetailConfig> getDtlsByOutSplitStgCfgId(String cfgId) throws Exception;
}
