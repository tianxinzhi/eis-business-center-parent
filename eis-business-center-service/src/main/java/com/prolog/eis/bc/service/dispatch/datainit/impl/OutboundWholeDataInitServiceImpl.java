package com.prolog.eis.bc.service.dispatch.datainit.impl;

import com.google.common.collect.Lists;
import com.prolog.eis.bc.constant.OutboundStrategyConfigConstant;
import com.prolog.eis.bc.constant.OutboundTaskConstant;
import com.prolog.eis.bc.facade.dto.outbound.WholeOutTaskContainerDto;
import com.prolog.eis.bc.facade.vo.OutboundStrategyConfigVo;
import com.prolog.eis.bc.service.dispatch.datainit.OutboundWholeDataInitService;
import com.prolog.eis.bc.service.outboundtask.OutboundTaskService;
import com.prolog.eis.component.algorithm.composeorder.entity.BizOutTask;
import com.prolog.eis.component.algorithm.composeorder.entity.BizOutTaskDetail;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategySourceAreaConfig;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.InvStockAlgorithmDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.OutTaskAlgorithmDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.OutTaskDetailAlgorithmDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.WholeOutContainerDto;

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
    private OutboundTaskService outboundTaskService;

    @Override
    public WholeOutContainerDto findWholeOutData() {


        return null;
    }

    @Override
    public WholeOutTaskContainerDto findWholeOutData(OutboundStrategyConfigVo config) {
        if (null == config) {
            return null;
        }
        if (StringUtils.isEmpty(config.getTypeNo())) {
            return null;
        }
        if (config.getOutType() != OutboundStrategyConfigConstant.OUT_TYPE_WHOLE) {
            return null;
        }
        WholeOutTaskContainerDto result = new WholeOutTaskContainerDto();
        result.setInvStockAlgorithmDtoList(Lists.newArrayList());
        result.setOutTaskAlgorithmDtoList(Lists.newArrayList());;
        // 塞入出库任务数据
        List<OutTaskAlgorithmDto> outTaskAlgorithmDtoList = Lists.newArrayList();
        // 根据配置找到对应的出库任务，查询出必要信息
        // 查询typeNo在集合中的且状态=未开始or进行中的出库任务
        List<BizOutTask> outboundTaskList = outboundTaskService
                .getListByTypeNoListAndStateList(Lists.newArrayList(config.getTypeNo()),
                        Lists.newArrayList(OutboundTaskConstant.STATE_NOSTART,
                                OutboundTaskConstant.STATE_GOINGON));
        // 查询策略对应的出库任务单类型编号列表
        if (CollectionUtils.isEmpty(outboundTaskList)) {
            return result;
        }
        for (BizOutTask outboundTask : outboundTaskList) {
            OutTaskAlgorithmDto outTaskAlgorithmDto = new OutTaskAlgorithmDto();
            outTaskAlgorithmDto.setOutTaskId(outboundTask.getId());
            // 取出该outTaskId下的出库单明细
            List<BizOutTaskDetail> outboundTaskDetailListByOutTaskId = outboundTask.getBizOutTaskDetailList();
            if (CollectionUtils.isEmpty(outboundTaskDetailListByOutTaskId)) {
                outTaskAlgorithmDto.setOutTaskDetailList(Lists.newArrayList());
            } else {
                List<OutTaskDetailAlgorithmDto> outTaskDetailAlgorithmDtoList = Lists.newArrayList();
                // 将BizOutTaskDetail转化为OutTaskDetailAlgorithmDto
                for (BizOutTaskDetail outboundTaskDetail : outboundTaskDetailListByOutTaskId) {
                    OutTaskDetailAlgorithmDto outTaskDetailAlgorithmDto = new OutTaskDetailAlgorithmDto();
                    outTaskDetailAlgorithmDto.setOutTaskId(outboundTaskDetail.getOutTaskId());
                    outTaskDetailAlgorithmDto.setOutTaskDetailId(outboundTaskDetail.getId());
                    outTaskDetailAlgorithmDto.setPlanNum(outboundTaskDetail.getPlanNum());
                    if (config.getStoreMatchingStrategy() == OutboundStrategyConfigConstant.STORE_MATCHING_STRATEGY_IOT) {
                        // 按批次
                        outTaskDetailAlgorithmDto.setUniqueKey(outboundTaskDetail.getLotId());
                    } else {
                        // 按商品
                        outTaskDetailAlgorithmDto.setUniqueKey(outboundTaskDetail.getItemId());
                    }
                    outTaskDetailAlgorithmDto.setActualNum(outboundTaskDetail.getActualNum());
                    outTaskDetailAlgorithmDto.setBindingNum(outboundTaskDetail.getBindingNum());
                    outTaskDetailAlgorithmDtoList.add(outTaskDetailAlgorithmDto);
                }
                outTaskAlgorithmDto.setOutTaskDetailList(outTaskDetailAlgorithmDtoList);
            }
            outTaskAlgorithmDtoList.add(outTaskAlgorithmDto);
        }

        // 塞入库存数据
        List<InvStockAlgorithmDto> invStockAlgorithmDtoList = Lists.newArrayList();
        List<OutboundStrategySourceAreaConfig> saConfigList = config.getOutboundStrategySourceAreaConfigList();
        if (!CollectionUtils.isEmpty(saConfigList)) {
            List<String> areaNoList = saConfigList.stream().filter(e -> !StringUtils.isEmpty(e.getAreaNo())).map(e -> e.getAreaNo()).collect(Collectors.toList());
            
        }
        
        
        result.setOutTaskAlgorithmDtoList(outTaskAlgorithmDtoList);
        result.setInvStockAlgorithmDtoList(invStockAlgorithmDtoList);
        return result;
    }
}
