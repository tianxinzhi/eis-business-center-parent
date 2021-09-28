package com.prolog.eis.bc.service.policy;

import java.util.List;

import com.prolog.eis.bc.facade.dto.policy.OutStgDto;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategySourceAreaConfig;

/**
 * @Author hzw
 * @Date 2021/9/27
 **/
public interface OutStgSaService {

	//起点区域修改
	void editSourceArea(OutStgDto dto);

	//起点区域查询
	List<OutboundStrategySourceAreaConfig> detailSourceArea(OutStgDto dto);
}
