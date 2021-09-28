package com.prolog.eis.bc.service.policy;

import com.prolog.eis.bc.facade.dto.policy.OutStgDto;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategyConfig;
import com.prolog.framework.core.pojo.Page;

/**
 * @Author hzw
 * @Date 2021/9/27
 **/
public interface OutStgService {

    //出库任务单策略配置查询
	Page<OutboundStrategyConfig> getOutStg(OutStgDto dto);

	//出库任务单策略配置新增
	void addOutStg(OutboundStrategyConfig dto);

	//出库任务单策略配置修改
	void editOutStg(OutboundStrategyConfig dto);

	//出库任务单策略配置删除
	void deleteOutStg(OutboundStrategyConfig dto);
}
