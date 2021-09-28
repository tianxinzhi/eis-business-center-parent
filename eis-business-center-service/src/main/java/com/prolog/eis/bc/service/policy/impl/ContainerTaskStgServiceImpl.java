package com.prolog.eis.bc.service.policy.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prolog.eis.bc.dao.policy.ContainerTaskStgMapper;
import com.prolog.eis.bc.dao.policy.ContainerTaskStgSaMapper;
import com.prolog.eis.bc.dao.policy.ContainerTaskStgTaMapper;
import com.prolog.eis.bc.facade.dto.policy.ContainerTaskStrategyDto;
import com.prolog.eis.bc.service.policy.ContainerTaskStgService;
import com.prolog.eis.core.model.ctrl.container.ContainerTaskStrategy;
import com.prolog.eis.core.model.ctrl.container.ContainerTaskStrategySourceArea;
import com.prolog.eis.core.model.ctrl.container.ContainerTaskStrategyTargetArea;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategyConfig;
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
public class ContainerTaskStgServiceImpl implements ContainerTaskStgService {
	
	@Autowired
    private ContainerTaskStgMapper containerTaskStgMapper;
	@Autowired
    private ContainerTaskStgTaMapper containerTaskStgTaMapper;
	@Autowired
    private ContainerTaskStgSaMapper containerTaskStgSaMapper;

	//容器任务单策略查询列表
    @Override
    public Page<ContainerTaskStrategy> getContainerTaskStg(ContainerTaskStrategyDto dto) {
        if( StringUtils.isEmpty(dto.getPageSize()) || StringUtils.isEmpty(dto.getPageNum())) {
            throw new RuntimeException("请传入对应的分页参数");
        }
        //查询list返回结果
        List<ContainerTaskStrategy> list = containerTaskStgMapper.getContainerTaskStg(dto);
        return PageUtils.getPage(list);
    }
    
    //容器任务单策略新增
    @Override
    @Transactional
	public void addContainerTaskStg(ContainerTaskStrategy dto) {
    	if( StringUtils.isEmpty(dto.getContainerTaskTypeNo())) {
            throw new RuntimeException("编号不能为空");
        }
		List<ContainerTaskStrategy> containerTaskStrategy = containerTaskStgMapper.bfindByMap(MapUtils.put("containerTaskTypeNo", dto.getContainerTaskTypeNo()).getMap(), ContainerTaskStrategy.class);
		if(containerTaskStrategy.size()>0) {
			throw new RuntimeException("当前编号已存在");
		}
		dto.setCreateTime(new Date());
		containerTaskStgMapper.save(dto);
	}

    //容器任务单策略修改
	@Override
	@Transactional
	public void editContainerTaskStg(ContainerTaskStrategy dto) {
		if( StringUtils.isEmpty(dto.getId())) {
            throw new RuntimeException("主键不能为空");
        }
		
		Criteria criteria = Criteria.forClass(ContainerTaskStrategy.class);
		criteria.setRestriction(Restrictions.and(Restrictions.eq("containerTaskTypeNo", dto.getContainerTaskTypeNo()),Restrictions.ne("id", dto.getId())));
        List<ContainerTaskStrategy> containerTaskStrategy = containerTaskStgMapper.findByCriteria(criteria);
		
		if(containerTaskStrategy.size()>0) {
			throw new RuntimeException("当前编号已存在");
		}
        //修改
		try {
			ContainerTaskStrategy data = containerTaskStgMapper.findById(dto.getId(), ContainerTaskStrategy.class);
			data.setContainerTaskTypeNo(dto.getContainerTaskTypeNo());
			data.setTypeName(dto.getTypeName());
			containerTaskStgMapper.update(data);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	//容器任务单策略删除
	@Override
	@Transactional
	public void deleteContainerTaskStg(ContainerTaskStrategy dto) {
		// TODO Auto-generated method stub
		if( StringUtils.isEmpty(dto.getId())) {
            throw new RuntimeException("主键不能为空");
        }
		containerTaskStgTaMapper.deleteByMap(MapUtils.put("containerTaskStgId", dto.getId()).getMap(), ContainerTaskStrategyTargetArea.class);
		containerTaskStgSaMapper.deleteByMap(MapUtils.put("containerTaskStrategyId", dto.getId()).getMap(), ContainerTaskStrategySourceArea.class);
		containerTaskStgMapper.deleteById(dto.getId(), ContainerTaskStrategy.class);
	}

}
