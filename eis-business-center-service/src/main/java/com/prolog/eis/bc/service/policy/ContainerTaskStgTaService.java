package com.prolog.eis.bc.service.policy;

import java.util.List;

import com.prolog.eis.bc.facade.dto.policy.ContainerTaskStrategyDto;
import com.prolog.eis.core.model.ctrl.container.ContainerTaskStrategyTargetArea;

/**
 * @Author hzw
 * @Date 2021/9/27
 **/
public interface ContainerTaskStgTaService {

    //终点区域修改
	void editTargetArea(ContainerTaskStrategyDto dto);

	//终点区域查询
	List<ContainerTaskStrategyTargetArea> detailTargetArea(ContainerTaskStrategyDto dto);
}
