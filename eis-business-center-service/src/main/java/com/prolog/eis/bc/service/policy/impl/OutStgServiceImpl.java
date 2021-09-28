package com.prolog.eis.bc.service.policy.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prolog.eis.bc.dao.OutboundStrategyConfigMapper;
import com.prolog.eis.bc.dao.OutboundStrategySourceAreaConfigMapper;
import com.prolog.eis.bc.dao.OutboundStrategyTargetStationConfigMapper;
import com.prolog.eis.bc.facade.dto.policy.OutStgDto;
import com.prolog.eis.bc.service.policy.OutStgService;
import com.prolog.eis.core.model.ctrl.container.ContainerTaskStrategy;
import com.prolog.eis.core.model.ctrl.container.ContainerTaskStrategySourceArea;
import com.prolog.eis.core.model.ctrl.container.ContainerTaskStrategyTargetArea;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategyConfig;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategySourceAreaConfig;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategyTargetStationConfig;
import com.prolog.framework.core.pojo.Page;
import com.prolog.framework.core.restriction.Criteria;
import com.prolog.framework.core.restriction.Restrictions;
import com.prolog.framework.dao.util.PageUtils;
import com.prolog.framework.toolkit.MapToolKit;
import com.prolog.framework.utils.MapUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @Describe
 * @Author hzw
 * @Date 2021/9/27
 **/
@Service
@Slf4j
public class OutStgServiceImpl implements OutStgService {
	
	@Autowired
    private OutboundStrategyConfigMapper outboundStrategyConfigMapper;
	@Autowired
    private OutboundStrategySourceAreaConfigMapper outboundStrategySourceAreaConfigMapper;
	@Autowired
    private OutboundStrategyTargetStationConfigMapper outboundStrategyTargetStationConfigMapper;
	

	//出库任务单策略配置查询
    @Override
    public Page<OutboundStrategyConfig> getOutStg(OutStgDto dto) {
        if( StringUtils.isEmpty(dto.getPageSize()) || StringUtils.isEmpty(dto.getPageNum())) {
            throw new RuntimeException("请传入对应的分页参数");
        }
        //查询list返回结果
        List<OutboundStrategyConfig> list = outboundStrategyConfigMapper.getOutStg(dto);
        return PageUtils.getPage(list);
    }
    
    //出库任务单策略配置新增
    @Override
    @Transactional
	public void addOutStg(OutboundStrategyConfig dto) {
    	if( StringUtils.isEmpty(dto.getTypeNo())) {
            throw new RuntimeException("编号不能为空");
        }
		List<OutboundStrategyConfig> containerTaskStrategy = outboundStrategyConfigMapper.bfindByMap(MapUtils.put("typeNo", dto.getTypeNo()).getMap(), OutboundStrategyConfig.class);
		if(containerTaskStrategy.size()>0) {
			throw new RuntimeException("当前编号已存在");
		}
		outboundStrategyConfigMapper.save(dto);
	}

    //出库任务单策略配置修改
	@Override
	@Transactional
	public void editOutStg(OutboundStrategyConfig dto) {
		// TODO Auto-generated method stub
		if( StringUtils.isEmpty(dto.getId())) {
            throw new RuntimeException("主键不能为空");
        }
		
		Criteria criteria = Criteria.forClass(OutboundStrategyConfig.class);
		criteria.setRestriction(Restrictions.and(Restrictions.eq("typeNo", dto.getTypeNo()),Restrictions.ne("id", dto.getId())));
        List<OutboundStrategyConfig> outboundStrategyConfig = outboundStrategyConfigMapper.findByCriteria(criteria);
        
		if(outboundStrategyConfig.size()>0) {
			throw new RuntimeException("当前编号已存在");
		}
        //修改
		try {
			OutboundStrategyConfig data = outboundStrategyConfigMapper.findById(dto.getId(), OutboundStrategyConfig.class);
			data.setTypeNo(dto.getTypeNo());
			data.setTypeName(dto.getTypeName());
			data.setOutModel(dto.getOutModel());
			data.setDispatchPriority(dto.getDispatchPriority());
			data.setMaxOrderNum(dto.getMaxOrderNum());
			data.setMaxOrderVolume(dto.getMaxOrderVolume());
			data.setStoreMatchingStrategy(dto.getStoreMatchingStrategy());
			data.setOutboundExpiryDateRate(dto.getOutboundExpiryDateRate());
			data.setProhibitExpiryDateRate(dto.getProhibitExpiryDateRate());
			data.setClearStoreStrategy(dto.getClearStoreStrategy());
			data.setComposeOrderConfig(dto.getComposeOrderConfig());
			data.setMaxItemNum(dto.getMaxItemNum());
			outboundStrategyConfigMapper.update(data);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	//容器任务单策略删除
	@Override
	@Transactional
	public void deleteOutStg(OutboundStrategyConfig dto) {
		// TODO Auto-generated method stub
		if( StringUtils.isEmpty(dto.getId())) {
            throw new RuntimeException("主键不能为空");
        }
		outboundStrategyTargetStationConfigMapper.deleteByMap(MapUtils.put("outStgCfgId", dto.getId()).getMap(), OutboundStrategyTargetStationConfig.class);
		outboundStrategySourceAreaConfigMapper.deleteByMap(MapUtils.put("outStgCfgId", dto.getId()).getMap(), OutboundStrategySourceAreaConfig.class);
		outboundStrategyConfigMapper.deleteById(dto.getId(), OutboundStrategyConfig.class);
	}

}
