package com.prolog.eis.bc.service.policy;

import java.util.List;

import com.prolog.eis.bc.facade.dto.policy.OutStgDto;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategyTargetStationConfig;

/**
 * @Author hzw
 * @Date 2021/9/27
 **/
public interface OutStgTaService {

	//终点区域修改
	void editTargetArea(OutStgDto dto);

	//终点区域查询
	List<OutboundStrategyTargetStationConfig> detailTargetArea(OutStgDto dto);
}
