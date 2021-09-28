package com.prolog.eis.bc.service.policy;

import java.util.List;

import com.prolog.eis.bc.facade.dto.policy.ContainerTaskStrategyDto;
import com.prolog.eis.core.model.ctrl.container.ContainerTaskStrategySourceArea;

/**
 * @Author hzw
 * @Date 2021/9/27
 **/
public interface ContainerTaskStgSaService {

	//起点区域修改
	void editSourceArea(ContainerTaskStrategyDto dto);

	//起点区域查询
	List<ContainerTaskStrategySourceArea> detailSourceArea(ContainerTaskStrategyDto dto);
}
