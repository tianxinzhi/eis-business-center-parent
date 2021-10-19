package com.prolog.eis.bc.service.sscdtl.impl;

import com.prolog.eis.bc.dao.sscdtl.OutboundSplitStrategyDetailConfigMapper;
import com.prolog.eis.bc.facade.dto.policy.OutSplitStgDto;
import com.prolog.eis.bc.service.sscdtl.OutboundSplitStrategyDetailConfigService;
import com.prolog.eis.core.model.ctrl.outbound.OutboundSplitStrategyDetailConfig;
import com.prolog.framework.core.restriction.Criteria;
import com.prolog.framework.core.restriction.Restrictions;
import com.prolog.framework.utils.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * @Author: txz
 * @Date: 2021/9/23 16:56
 * @Desc: 出库拆单策略明细
 */
@Service
public class OutboundSplitStrategyDetailConfigServiceImpl implements OutboundSplitStrategyDetailConfigService {

    @Autowired
    private OutboundSplitStrategyDetailConfigMapper mapper;

    @Override
    public List<OutboundSplitStrategyDetailConfig> getDtlsByOutSplitStgCfgId(String cfgId) throws Exception{
        Assert.notNull(cfgId,"出库拆单策略类型id不可为空");
        Criteria criteria = new Criteria(OutboundSplitStrategyDetailConfig.class);
        criteria.setRestriction(Restrictions.eq("outSplitStgCfgId",cfgId));
        List<OutboundSplitStrategyDetailConfig> strategyDetails = mapper.findByCriteria(criteria);
        return strategyDetails;
    }

	@Override
	public void editOutSplit(OutSplitStgDto dto) {
		if(StringUtils.isEmpty(dto.getId())) {
            throw new RuntimeException("编号不能为空");
        }
		mapper.deleteByMap(MapUtils.put("outStgCfgId", dto.getId()).getMap(), OutboundSplitStrategyDetailConfig.class);
		if(dto.getAreaList()!=null) {
			for (Map area:dto.getAreaList()) {
				OutboundSplitStrategyDetailConfig data = new OutboundSplitStrategyDetailConfig();
				data.setOutSplitStgCfgId(dto.getId());
				data.setAreaNo(area.get("areaNo").toString());
				data.setSplitStrategy(area.get("splitStrategy").toString());
				data.setTaskConfigTypeNo(area.get("taskConfigTypeNo").toString());
				data.setSortIndex(Integer.valueOf(area.get("sortIndex").toString()));
				//data.setAvgOutboundTime(area.get("avgOutboundTime").toString());
				data.setCargoOwnerId(dto.getCargoOwnerId());
				data.setEnterpriseId(dto.getEnterpriseId());
				data.setWarehouseId(dto.getWarehouseId());
				mapper.save(data);
			}
		}
	}

	@Override
	public List<OutboundSplitStrategyDetailConfig> detailOutSplit(OutSplitStgDto dto) {
		if(StringUtils.isEmpty(dto.getId())) {
		  throw new RuntimeException("编号不能为空");
		}
		return mapper.findByMap(MapUtils.put("outSplitStgCfgId", dto.getId()).getMap(), OutboundSplitStrategyDetailConfig.class);
	}
}
