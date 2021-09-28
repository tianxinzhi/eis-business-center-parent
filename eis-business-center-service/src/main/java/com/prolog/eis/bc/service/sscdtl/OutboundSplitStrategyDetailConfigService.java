package com.prolog.eis.bc.service.sscdtl;

import java.util.List;

import com.prolog.eis.bc.facade.dto.policy.OutSplitStgDto;
import com.prolog.eis.bc.facade.dto.policy.OutStgDto;
import com.prolog.eis.core.model.ctrl.outbound.OutboundSplitStrategyDetailConfig;

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
    
    //修改明细	by HZW
  	void editOutSplit(OutSplitStgDto dto);

  	//查询明细	by HZW
  	List<OutboundSplitStrategyDetailConfig> detailOutSplit(OutSplitStgDto dto);
}
