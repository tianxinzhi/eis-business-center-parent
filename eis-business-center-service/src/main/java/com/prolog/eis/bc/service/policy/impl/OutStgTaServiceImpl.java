package com.prolog.eis.bc.service.policy.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prolog.eis.bc.dao.OutboundStrategyTargetStationConfigMapper;
import com.prolog.eis.bc.facade.dto.policy.ContainerTaskStrategyDto;
import com.prolog.eis.bc.facade.dto.policy.OutStgDto;
import com.prolog.eis.bc.service.policy.OutStgTaService;
import com.prolog.eis.core.model.ctrl.container.ContainerTaskStrategyTargetArea;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategyTargetStationConfig;
import com.prolog.framework.utils.MapUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @Describe
 * @Author hzw
 * @Date 2021/9/27
 **/
@Service
@Slf4j
public class OutStgTaServiceImpl implements OutStgTaService {
	
	@Autowired
    private OutboundStrategyTargetStationConfigMapper outboundStrategyTargetStationConfigMapper;

	//终点区域修改
	@Override
	@Transactional
	public void editTargetArea(OutStgDto dto) {
		if(StringUtils.isEmpty(dto.getId())) {
            throw new RuntimeException("编号不能为空");
        }
		outboundStrategyTargetStationConfigMapper.deleteByMap(MapUtils.put("outStgCfgId", dto.getId()).getMap(), OutboundStrategyTargetStationConfig.class);
		if(dto.getAreaList()!=null) {
			for (Map area:dto.getAreaList()) {
				OutboundStrategyTargetStationConfig data = new OutboundStrategyTargetStationConfig();
				data.setOutStgCfgId(dto.getId());
				data.setStationId(area.get("areaNo").toString());
				data.setPriority(area.get("priority").toString());
				data.setCargoOwnerId(dto.getCargoOwnerId());
				data.setEnterpriseId(dto.getEnterpriseId());
				data.setWarehouseId(dto.getWarehouseId());
				outboundStrategyTargetStationConfigMapper.save(data);
			}
		}
	}
	
	//终点区域查询
	@Override
	public List<OutboundStrategyTargetStationConfig> detailTargetArea(OutStgDto dto) {
		if(StringUtils.isEmpty(dto.getId())) {
	      throw new RuntimeException("编号不能为空");
	    }
		return outboundStrategyTargetStationConfigMapper.findByMap(MapUtils.put("outStgCfgId", dto.getId()).getMap(), OutboundStrategyTargetStationConfig.class);
	}

}
