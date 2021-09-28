package com.prolog.eis.bc.service.policy;

import com.prolog.eis.bc.facade.dto.policy.ContainerTaskStrategyDto;
import com.prolog.eis.core.model.ctrl.container.ContainerTaskStrategy;
import com.prolog.framework.core.pojo.Page;

/**
 * @Author hzw
 * @Date 2021/9/27
 **/
public interface ContainerTaskStgService {

    //容器任务单策略查询
	Page<ContainerTaskStrategy> getContainerTaskStg(ContainerTaskStrategyDto dto);
	
	//容器任务单策略新增
	void addContainerTaskStg(ContainerTaskStrategy dto);

	//容器任务单策略修改
	void editContainerTaskStg(ContainerTaskStrategy dto);

	//容器任务单策略删除
	void deleteContainerTaskStg(ContainerTaskStrategy dto);
}
