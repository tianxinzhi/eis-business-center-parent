package com.prolog.eis.bc.service.policy.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prolog.eis.bc.dao.OutboundStrategySourceAreaConfigMapper;
import com.prolog.eis.bc.facade.dto.policy.OutStgDto;
import com.prolog.eis.bc.service.policy.OutStgSaService;
import com.prolog.eis.core.model.ctrl.container.ContainerTaskStrategyTargetArea;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategySourceAreaConfig;
import com.prolog.framework.utils.MapUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @Describe
 * @Author hzw
 * @Date 2021/9/27
 **/
@Service
@Slf4j
public class OutStgSaServiceImpl implements OutStgSaService {
	
	@Autowired
    private OutboundStrategySourceAreaConfigMapper outboundStrategySourceAreaConfigMapper;

	//起点区域修改
	@Override
	@Transactional
	public void editSourceArea(OutStgDto dto) {
		if(StringUtils.isEmpty(dto.getId())) {
            throw new RuntimeException("编号不能为空");
        }
		outboundStrategySourceAreaConfigMapper.deleteByMap(MapUtils.put("outStgCfgId", dto.getId()).getMap(), OutboundStrategySourceAreaConfig.class);
		if(dto.getAreaList()!=null) {
			for (Map area:dto.getAreaList()) {
				OutboundStrategySourceAreaConfig data = new OutboundStrategySourceAreaConfig();
				data.setOutStgCfgId(dto.getId());
				data.setAreaNo(area.get("areaNo").toString());
				data.setPriority(Integer.parseInt(area.get("priority").toString()));
				data.setCargoOwnerId(dto.getCargoOwnerId());
				data.setEnterpriseId(dto.getEnterpriseId());
				data.setWarehouseId(dto.getWarehouseId());
				outboundStrategySourceAreaConfigMapper.save(data);
			}
		}
	}
	
	//起点区域查询
	@Override
	public List<OutboundStrategySourceAreaConfig> detailSourceArea(OutStgDto dto) {
		if(StringUtils.isEmpty(dto.getId())) {
	      throw new RuntimeException("编号不能为空");
	    }
		return outboundStrategySourceAreaConfigMapper.findByMap(MapUtils.put("outStgCfgId", dto.getId()).getMap(), OutboundStrategySourceAreaConfig.class);
	}

}
