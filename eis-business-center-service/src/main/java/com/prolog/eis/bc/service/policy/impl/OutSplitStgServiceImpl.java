package com.prolog.eis.bc.service.policy.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prolog.eis.bc.dao.ssc.OutboundSplitStrategyConfigMapper;
import com.prolog.eis.bc.dao.sscdtl.OutboundSplitStrategyDetailConfigMapper;
import com.prolog.eis.bc.facade.dto.policy.OutSplitStgDto;
import com.prolog.eis.bc.service.policy.OutSplitStgService;
import com.prolog.eis.core.model.ctrl.container.ContainerTaskStrategy;
import com.prolog.eis.core.model.ctrl.outbound.OutboundSplitStrategyConfig;
import com.prolog.eis.core.model.ctrl.outbound.OutboundSplitStrategyDetailConfig;
import com.prolog.framework.core.pojo.Page;
import com.prolog.framework.core.restriction.Criteria;
import com.prolog.framework.core.restriction.Restrictions;
import com.prolog.framework.dao.util.PageUtils;
import com.prolog.framework.utils.MapUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @Describe
 * @Author hzw
 * @Date 2021/9/27
 **/
@Service
@Slf4j
public class OutSplitStgServiceImpl implements OutSplitStgService {
	
	@Autowired
    private OutboundSplitStrategyConfigMapper outboundSplitStrategyConfigMapper;
	@Autowired
    private OutboundSplitStrategyDetailConfigMapper outboundSplitStrategyDetailConfigMapper;
	

	//出库任务汇总单拆单策略配置查询
    @Override
    public Page<OutboundSplitStrategyConfig> getOutSplitStg(OutSplitStgDto dto) {
        if( StringUtils.isEmpty(dto.getPageSize()) || StringUtils.isEmpty(dto.getPageNum())) {
            throw new RuntimeException("请传入对应的分页参数");
        }
        //查询list返回结果
        List<OutboundSplitStrategyConfig> list = outboundSplitStrategyConfigMapper.getOutSplitStg(dto);
        return PageUtils.getPage(list);
    }
    
    //出库任务汇总单拆单策略配置新增
    @Override
    @Transactional
	public void addOutSplitStg(OutboundSplitStrategyConfig dto) {
    	if( StringUtils.isEmpty(dto.getStrategyTypeNo())) {
            throw new RuntimeException("编号不能为空");
        }
		List<OutboundSplitStrategyConfig> containerTaskStrategy = outboundSplitStrategyConfigMapper.bfindByMap(MapUtils.put("strategyTypeNo", dto.getStrategyTypeNo()).getMap(), OutboundSplitStrategyConfig.class);
		if(containerTaskStrategy.size()>0) {
			throw new RuntimeException("当前编号已存在");
		}
		outboundSplitStrategyConfigMapper.save(dto);
	}

    //出库任务汇总单拆单策略配置修改
	@Override
	@Transactional
	public void editOutSplitStg(OutboundSplitStrategyConfig dto) {
		// TODO Auto-generated method stub
		if( StringUtils.isEmpty(dto.getId())) {
            throw new RuntimeException("主键不能为空");
        }
		
		Criteria criteria = Criteria.forClass(OutboundSplitStrategyConfig.class);
		criteria.setRestriction(Restrictions.and(Restrictions.eq("strategyTypeNo", dto.getStrategyTypeNo()),Restrictions.ne("id", dto.getId())));
        List<OutboundSplitStrategyConfig> outboundSplitStrategyConfig = outboundSplitStrategyConfigMapper.findByCriteria(criteria);
		
		if(outboundSplitStrategyConfig.size()>0) {
			throw new RuntimeException("当前编号已存在");
		}
        //修改
		try {
			OutboundSplitStrategyConfig data = outboundSplitStrategyConfigMapper.findById(dto.getId(), OutboundSplitStrategyConfig.class);
			data.setStrategyTypeNo(dto.getStrategyTypeNo());
			data.setStrategyName(dto.getStrategyName());
			outboundSplitStrategyConfigMapper.update(data);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	//出库任务汇总单拆单策略配置删除
	@Override
	@Transactional
	public void deleteOutSplitStg(OutboundSplitStrategyConfig dto) {
		// TODO Auto-generated method stub
		if( StringUtils.isEmpty(dto.getId())) {
            throw new RuntimeException("主键不能为空");
        }
		outboundSplitStrategyDetailConfigMapper.deleteByMap(MapUtils.put("outSplitStgCfgId", dto.getId()).getMap(), OutboundSplitStrategyDetailConfig.class);
		outboundSplitStrategyConfigMapper.deleteById(dto.getId(), OutboundSplitStrategyConfig.class);
	}

}
