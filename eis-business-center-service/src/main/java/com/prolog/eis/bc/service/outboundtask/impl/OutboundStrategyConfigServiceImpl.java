package com.prolog.eis.bc.service.outboundtask.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.prolog.eis.bc.dao.OutboundStrategyConfigMapper;
import com.prolog.eis.bc.dao.OutboundStrategySourceAreaConfigMapper;
import com.prolog.eis.bc.dao.OutboundStrategyTargetStationConfigMapper;
import com.prolog.eis.bc.facade.vo.OutboundStrategyConfigVo;
import com.prolog.eis.bc.service.outboundtask.OutboundStrategyConfigService;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategyConfig;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategySourceAreaConfig;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategyTargetStationConfig;
import com.prolog.framework.core.exception.PrologException;
import com.prolog.framework.core.restriction.Criteria;
import com.prolog.framework.core.restriction.Restrictions;
import com.prolog.framework.utils.MapUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/9/15
 **/
@Service
public class OutboundStrategyConfigServiceImpl implements OutboundStrategyConfigService {

    @Autowired
    private OutboundStrategyConfigMapper outboundStrategyConfigMapper;
    @Autowired
    private OutboundStrategySourceAreaConfigMapper outboundStrategySourceAreaConfigMapper;
    @Autowired
    private OutboundStrategyTargetStationConfigMapper outboundStrategyTargetStationConfigMapper;

    @Override
    public OutboundStrategyConfigVo findConfigByTypeNo(String typeNo) throws PrologException {
        List<OutboundStrategyConfig> outboundStrategyConfigList = outboundStrategyConfigMapper.findByMap(MapUtils.put("typeNo", typeNo).getMap(), OutboundStrategyConfig.class);
        if (outboundStrategyConfigList.isEmpty()) {
            throw new PrologException(String.format("出库策略配置类型[%s]不存在,请配置", typeNo));
        }
        if (outboundStrategyConfigList.size() > 1) {
            throw new PrologException(String.format("出库策略配置类型[%s]存在多个,请检查配置", typeNo));
        }
        OutboundStrategyConfig outboundStrategyConfig = outboundStrategyConfigList.get(0);
        OutboundStrategyConfigVo outboundStrategyConfigVo = copyBean(outboundStrategyConfig);
        List<OutboundStrategySourceAreaConfig> outboundStrategySourceAreaConfigList = outboundStrategySourceAreaConfigMapper.findByMap(MapUtils.put("outStgCfgId", outboundStrategyConfigVo.getId()).getMap(), OutboundStrategySourceAreaConfig.class);
        List<OutboundStrategyTargetStationConfig> outboundStrategyTargetStationConfigList = outboundStrategyTargetStationConfigMapper.findByMap(MapUtils.put("outStgCfgId", outboundStrategyConfigVo.getId()).getMap(), OutboundStrategyTargetStationConfig.class);
        outboundStrategyConfigVo.setOutboundStrategySourceAreaConfigList(outboundStrategySourceAreaConfigList);
        outboundStrategyConfigVo.setOutboundStrategyTargetStationConfigList(outboundStrategyTargetStationConfigList);
        return outboundStrategyConfigVo;
    }

    @Override
    public List<OutboundStrategyConfig> getByOutModel(String outModel) throws PrologException {
        Assert.notNull(outModel, "出库模式不能为空");
        Criteria criteria = new Criteria(OutboundStrategyConfig.class);
        criteria.setRestriction(Restrictions.eq("outModel", outModel));
        List<OutboundStrategyConfig> confs = outboundStrategyConfigMapper.findByCriteria(criteria);
        return confs;
    }

    private OutboundStrategyConfigVo copyBean(OutboundStrategyConfig outboundStrategyConfig) {
        OutboundStrategyConfigVo outboundStrategyConfigVo = new OutboundStrategyConfigVo();
        outboundStrategyConfigVo.setId(outboundStrategyConfig.getId());
        outboundStrategyConfigVo.setTypeNo(outboundStrategyConfig.getTypeNo());
        outboundStrategyConfigVo.setOutModel(outboundStrategyConfig.getOutModel());
        outboundStrategyConfigVo.setTypeName(outboundStrategyConfig.getTypeName());
        outboundStrategyConfigVo.setStoreMatchingStrategy(outboundStrategyConfig.getStoreMatchingStrategy());
        outboundStrategyConfigVo.setProhibitExpiryDateRate(outboundStrategyConfig.getProhibitExpiryDateRate());
        outboundStrategyConfigVo.setOutboundExpiryDateRate(outboundStrategyConfig.getOutboundExpiryDateRate());
        outboundStrategyConfigVo.setMaxOrderVolume(outboundStrategyConfig.getMaxOrderVolume());
        outboundStrategyConfigVo.setMaxOrderNum(outboundStrategyConfig.getMaxOrderNum());
        outboundStrategyConfigVo.setOutType(outboundStrategyConfig.getOutType());
        outboundStrategyConfigVo.setDispatchPriority(outboundStrategyConfig.getDispatchPriority());
        outboundStrategyConfigVo.setComposeOrderConfig(outboundStrategyConfig.getComposeOrderConfig());
        outboundStrategyConfigVo.setClearStoreStrategy(outboundStrategyConfig.getClearStoreStrategy());
        return outboundStrategyConfigVo;
    }


    @Override
    public List<OutboundStrategyConfigVo> findAll() {
        List<OutboundStrategyConfigVo> outboundStrategyConfigVoList = new ArrayList<>();
        List<OutboundStrategyConfig> outboundStrategyConfigList = outboundStrategyConfigMapper.findByMap(null, OutboundStrategyConfig.class);
        if (outboundStrategyConfigList.isEmpty()) {
            return outboundStrategyConfigVoList;
        }
        for(OutboundStrategyConfig outboundStrategyConfig : outboundStrategyConfigList){
            OutboundStrategyConfigVo outboundStrategyConfigVo = copyBean(outboundStrategyConfig);
            List<OutboundStrategySourceAreaConfig> outboundStrategySourceAreaConfigList = outboundStrategySourceAreaConfigMapper.findByMap(MapUtils.put("outStgCfgId", outboundStrategyConfigVo.getId()).getMap(), OutboundStrategySourceAreaConfig.class);
            List<OutboundStrategyTargetStationConfig> outboundStrategyTargetStationConfigList = outboundStrategyTargetStationConfigMapper.findByMap(MapUtils.put("outStgCfgId", outboundStrategyConfigVo.getId()).getMap(), OutboundStrategyTargetStationConfig.class);
            outboundStrategyConfigVo.setOutboundStrategySourceAreaConfigList(outboundStrategySourceAreaConfigList);
            outboundStrategyConfigVo.setOutboundStrategyTargetStationConfigList(outboundStrategyTargetStationConfigList);
            outboundStrategyConfigVoList.add(outboundStrategyConfigVo);
        }
        return outboundStrategyConfigVoList;
    }

    @Override
    public List<OutboundStrategyConfigVo> getByOutType(int outType)
            throws PrologException {
        Criteria cfgCrt = new Criteria(OutboundStrategyConfig.class);
        cfgCrt.setRestriction(Restrictions.eq("outType", outType));
        List<OutboundStrategyConfig> configList = outboundStrategyConfigMapper.findByCriteria(cfgCrt);
        if (CollectionUtils.isEmpty(configList)) {
            return Lists.newArrayList();
        }

        List<String> configIdList = configList.stream().map(OutboundStrategyConfig::getId).collect(Collectors.toList());
        Criteria cfgSaCrt = new Criteria(OutboundStrategySourceAreaConfig.class);
        cfgCrt.setRestriction(Restrictions.in("outStgCfgId", configIdList.toArray()));

        List<OutboundStrategySourceAreaConfig> configSaList = outboundStrategySourceAreaConfigMapper.findByCriteria(cfgSaCrt);
        Map<String, List<OutboundStrategySourceAreaConfig>> configSaListGroupByConfigIdMap = configSaList.stream().filter(e -> !StringUtils.isEmpty(e.getOutStgCfgId())).collect(Collectors.groupingBy(OutboundStrategySourceAreaConfig::getOutStgCfgId));
        List<OutboundStrategyConfigVo> configVoList = Lists.newArrayList();
        for (OutboundStrategyConfig config : configList) {
            OutboundStrategyConfigVo configVo = new OutboundStrategyConfigVo();
            BeanUtils.copyProperties(config, configVo);
            configVo.setOutboundStrategySourceAreaConfigList(configSaListGroupByConfigIdMap.get(config.getId()));
            configVo.setOutboundStrategyTargetStationConfigList(Lists.newArrayList());
            configVoList.add(configVo);
        }
        return configVoList;
    }

}