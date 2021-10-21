package com.prolog.eis.bc.service.dispatch.datainit.impl;

import com.google.common.collect.Lists;
import com.prolog.eis.bc.constant.OutboundStrategyConfigConstant;
import com.prolog.eis.bc.constant.OutboundTaskConstant;
import com.prolog.eis.bc.service.dispatch.datainit.OutboundWholeDataInitService;
import com.prolog.eis.bc.service.outboundtask.OutboundStrategyConfigService;
import com.prolog.eis.bc.service.outboundtask.OutboundTaskService;
import com.prolog.eis.core.model.biz.outbound.OutboundTask;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategyConfig;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.WholeOutContainerDto;import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/10/21
 **/
@Service
public class OutboundWholeDataInitServiceImpl implements OutboundWholeDataInitService {

    @Autowired
    private OutboundStrategyConfigService outboundStrategyConfigService;

    @Autowired
    private OutboundTaskService outboundTaskService;

    @Override
    public WholeOutContainerDto findWholeOutData() {


        return null;
    }

    @Override
    public WholeOutContainerDto findWholeOutDataTmp() {
        // 查询所有outType=1的整托出库策略对象
        List<OutboundStrategyConfig> configList = outboundStrategyConfigService
                .getByOutType(OutboundStrategyConfigConstant.OUT_TYPE_WHOLE);
        // 查询策略对应的出库任务单类型编号列表
        List<String> typeNoList = configList.stream()
                .filter(e -> !StringUtils.isEmpty(e.getTypeNo()))
                .map(e -> e.getTypeNo()).collect(Collectors.toList());
        // 查询typeNo在集合中的且状态=未开始or进行中的出库任务
        List<OutboundTask> outboundTaskList = outboundTaskService
                .getListByTypeNoListAndStateList(typeNoList,
                        Lists.newArrayList(OutboundTaskConstant.STATE_NOSTART,
                                OutboundTaskConstant.STATE_GOINGON));
        if (CollectionUtils.isEmpty(outboundTaskList)) {
            return null;
        }
        for (OutboundTask outboundTask : outboundTaskList) {
            
        }
        
        
        
        return null;
    }
}
