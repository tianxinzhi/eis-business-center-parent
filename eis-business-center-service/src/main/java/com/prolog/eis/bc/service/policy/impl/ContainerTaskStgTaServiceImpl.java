package com.prolog.eis.bc.service.policy.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prolog.eis.bc.dao.policy.ContainerTaskStgTaMapper;
import com.prolog.eis.bc.facade.dto.policy.ContainerTaskStrategyDto;
import com.prolog.eis.bc.service.policy.ContainerTaskStgTaService;
import com.prolog.eis.core.model.ctrl.container.ContainerTaskStrategyTargetArea;
import com.prolog.framework.utils.MapUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @Describe
 * @Author hzw
 * @Date 2021/9/27
 **/
@Service
@Slf4j
public class ContainerTaskStgTaServiceImpl implements ContainerTaskStgTaService {
	
	@Autowired
    private ContainerTaskStgTaMapper containerTaskStgTaMapper;

	//终点区域修改
	@Override
	@Transactional
	public void editTargetArea(ContainerTaskStrategyDto dto) {
		if(StringUtils.isEmpty(dto.getId())) {
            throw new RuntimeException("编号不能为空");
        }
		containerTaskStgTaMapper.deleteByMap(MapUtils.put("containerTaskStgId", dto.getId()).getMap(), ContainerTaskStrategyTargetArea.class);
		if(dto.getAreaList()!=null) {
			for (String area:dto.getAreaList()) {
				ContainerTaskStrategyTargetArea containerTaskStrategyTargetArea = new ContainerTaskStrategyTargetArea();
				containerTaskStrategyTargetArea.setContainerTaskStgId(dto.getId());
				containerTaskStrategyTargetArea.setAreaNo(area);
				containerTaskStrategyTargetArea.setCargoOwnerId(dto.getCargoOwnerId());
				containerTaskStrategyTargetArea.setEnterpriseId(dto.getEnterpriseId());
				containerTaskStrategyTargetArea.setWarehouseId(dto.getWarehouseId());
				containerTaskStgTaMapper.save(containerTaskStrategyTargetArea);
			}
		}
	}
	
	//终点区域查询
	@Override
	public List<ContainerTaskStrategyTargetArea> detailTargetArea(ContainerTaskStrategyDto dto) {
		if(StringUtils.isEmpty(dto.getId())) {
	      throw new RuntimeException("编号不能为空");
	    }
		return containerTaskStgTaMapper.findByMap(MapUtils.put("containerTaskStgId", dto.getId()).getMap(), ContainerTaskStrategyTargetArea.class);
	}

}
