package com.prolog.eis.bc.service.policy;

import com.prolog.eis.bc.facade.dto.policy.OutSplitStgDto;
import com.prolog.eis.bc.facade.dto.policy.OutStgDto;
import com.prolog.eis.core.model.ctrl.outbound.OutboundSplitStrategyConfig;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategyConfig;
import com.prolog.framework.core.pojo.Page;

/**
 * @Author hzw
 * @Date 2021/9/27
 **/
public interface OutSplitStgService {

    //出库任务汇总单拆单策略配置查询
	Page<OutboundSplitStrategyConfig> getOutSplitStg(OutSplitStgDto dto);

	//出库任务汇总单拆单策略配置新增
	void addOutSplitStg(OutboundSplitStrategyConfig dto);

	//出库任务汇总单拆单策略配置修改
	void editOutSplitStg(OutboundSplitStrategyConfig dto);

	//出库任务汇总单拆单策略配置删除
	void deleteOutSplitStg(OutboundSplitStrategyConfig dto);

}
