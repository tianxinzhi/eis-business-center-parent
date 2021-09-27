package com.prolog.eis.bc.service.ssc;

import com.prolog.eis.core.model.ctrl.outbound.OutboundSplitStrategyConfig;

import java.util.List;

/**
 * @Author: txz
 * @Date: 2021/9/23 16:45
 */
public interface OutboundSplitStrategyConfigService {

    /**
     * 根据策略类型编号查找出库拆单策略信息
     * @param strategyTypeNo
     * @return
     */
    OutboundSplitStrategyConfig getByStrategyTypeNo(String strategyTypeNo) throws Exception;
}
