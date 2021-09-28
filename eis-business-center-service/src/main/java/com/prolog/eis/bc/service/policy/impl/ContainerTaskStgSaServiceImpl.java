package com.prolog.eis.bc.service.policy.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prolog.eis.bc.dao.policy.ContainerTaskStgSaMapper;
import com.prolog.eis.bc.facade.dto.policy.ContainerTaskStrategyDto;
import com.prolog.eis.bc.service.policy.ContainerTaskStgSaService;
import com.prolog.eis.core.model.ctrl.container.ContainerTaskStrategySourceArea;
import com.prolog.framework.utils.MapUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @Describe
 * @Author hzw
 * @Date 2021/9/27
 **/
@Service
@Slf4j
public class ContainerTaskStgSaServiceImpl implements ContainerTaskStgSaService {
	
	@Autowired
    private ContainerTaskStgSaMapper containerTaskStgSaMapper;
	
	//起点区域修改
	@Override
	@Transactional
	public void editSourceArea(ContainerTaskStrategyDto dto) {
		if(StringUtils.isEmpty(dto.getId())) {
            throw new RuntimeException("编号不能为空");
        }
		containerTaskStgSaMapper.deleteByMap(MapUtils.put("container_task_strategy_id", dto.getId()).getMap(), ContainerTaskStrategySourceArea.class);
		if(dto.getAreaList()!=null) {
			for (String area:dto.getAreaList()) {
				ContainerTaskStrategySourceArea containerTaskStrategySourceArea = new ContainerTaskStrategySourceArea();
				containerTaskStrategySourceArea.setContainerTaskStrategyId(dto.getId());
				containerTaskStrategySourceArea.setAreaNo(area);
				containerTaskStrategySourceArea.setCargoOwnerId(dto.getCargoOwnerId());
				containerTaskStrategySourceArea.setEnterpriseId(dto.getEnterpriseId());
				containerTaskStrategySourceArea.setWarehouseId(dto.getWarehouseId());
				containerTaskStgSaMapper.save(containerTaskStrategySourceArea);
			}
		}
	}

	//起点区域查询
	@Override
	public List<ContainerTaskStrategySourceArea> detailSourceArea(ContainerTaskStrategyDto dto) {
		if(StringUtils.isEmpty(dto.getId())) {
            throw new RuntimeException("编号不能为空");
        }
		return containerTaskStgSaMapper.findByMap(MapUtils.put("containerTaskStrategyId", dto.getId()).getMap(), ContainerTaskStrategySourceArea.class);
	}
}
