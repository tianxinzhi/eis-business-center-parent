package com.prolog.eis.bc.service.dispatch.impl;

import com.alibaba.fastjson.JSONObject;
import com.prolog.eis.bc.dao.OutboundTaskBindDetailMapper;
import com.prolog.eis.bc.dao.OutboundTaskBindMapper;
import com.prolog.eis.bc.facade.dto.outbound.WholeOutTaskContainerDto;
import com.prolog.eis.bc.facade.dto.outbound.WholeStationDto;
import com.prolog.eis.bc.facade.vo.OutboundStrategyConfigVo;
import com.prolog.eis.bc.feign.container.EisContainerRouteClient;
import com.prolog.eis.bc.feign.container.EisContainerStoreFeign;
import com.prolog.eis.bc.service.dispatch.OutWholeService;
import com.prolog.eis.bc.service.dispatch.datainit.OutboundWholeDataInitService;
import com.prolog.eis.bc.service.dispatch.strategy.OutboundStrategyContext;
import com.prolog.eis.bc.service.outboundtask.OutboundTaskBindService;
import com.prolog.eis.common.util.PrologStringUtils;
import com.prolog.eis.core.model.biz.carry.CarryTask;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskBind;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskBindDetail;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategyTargetStationConfig;
import com.prolog.framework.common.message.RestMessage;
import com.prolog.framework.core.exception.PrologException;
import com.prolog.framework.utils.MapUtils;
import com.prolog.framework.utils.StringUtils;
import com.prolog.upcloud.base.inventory.vo.EisInvContainerStoreSubVo;
import com.prolog.upcloud.base.inventory.vo.EisInvContainerStoreVo;
import com.prolog.upcloud.base.strategy.domain.core.Strategy;
import com.prolog.upcloud.base.strategy.dto.StrategyDTO;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.OutboundDataSourceDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.OutboundStrategyDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.OutTaskAlgorithmDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.OutTaskDetailAlgorithmDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.WholeOutContainerDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.WholeOutStrategyResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/10/22
 **/
@Service
@Slf4j
public class OutWholeServiceImpl implements OutWholeService {

    @Autowired
    private OutboundWholeDataInitService outboundWholeDataInitService;
    @Autowired
    private EisContainerRouteClient eisContainerRouteClient;
    @Autowired
    private OutboundTaskBindMapper outboundTaskBindMapper;
    @Autowired
    private EisContainerStoreFeign eisContainerStoreFeign;
    @Autowired
    private OutboundTaskBindDetailMapper outboundTaskBindDetailMapper;

    @Override
    public boolean outContainer(WholeStationDto wholeStationDto, WholeOutTaskContainerDto wholeOutTaskContainerDto, StrategyDTO data, OutboundStrategyTargetStationConfig outboundStrategyTargetStationConfig, OutboundStrategyConfigVo outboundStrategyConfigVo) throws Exception {
        boolean success = false;
        List<OutTaskAlgorithmDto> outTaskAlgorithmDtoList = wholeOutTaskContainerDto.getOutTaskAlgorithmDtoList();
        for (OutTaskAlgorithmDto outTaskAlgorithmDto : outTaskAlgorithmDtoList) {
            try {
                OutboundDataSourceDto outboundDataSourceDto = new OutboundDataSourceDto();
                WholeOutContainerDto wholeOutContainerDto = new WholeOutContainerDto();
                wholeOutContainerDto.setInvStockAlgorithmDtoList(wholeOutContainerDto.getInvStockAlgorithmDtoList());
                wholeOutContainerDto.setOutTaskAlgorithmDto(outTaskAlgorithmDto);
                wholeOutContainerDto.setOriginX(outboundStrategyTargetStationConfig.getX());
                wholeOutContainerDto.setOriginY(outboundStrategyTargetStationConfig.getY());
                outboundDataSourceDto.setWholeOutContainerDto(wholeOutContainerDto);
                Map<String, String> strategyType = new HashMap<>();
                strategyType.put("strategyType", "wholeOut");
                OutboundStrategyContext outboundStrategyContext = new OutboundStrategyContext(outboundWholeDataInitService);
                OutboundStrategyDto outboundStrategyDto = new OutboundStrategyDto();
                outboundStrategyDto.setStrategyType(strategyType);
                outboundStrategyDto.setCondition(wholeOutTaskContainerDto);
                Strategy strategy = data.createStrategy();
                strategy.execute(outboundStrategyContext);
                OutboundStrategyDto outboundStrategyDto1 = (OutboundStrategyDto) outboundStrategyContext.getStrategyData();
                List<WholeOutStrategyResultDto> result = (List<WholeOutStrategyResultDto>) outboundStrategyDto1.getResult();
                if (!result.isEmpty()) {
                    //生成容器绑定任务
                    //生成搬运任务
                    this.generateData(result, outTaskAlgorithmDto, wholeStationDto, outboundStrategyConfigVo);
                    success = true;
                    break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return success;
    }

    @Override
    public void generateData(List<WholeOutStrategyResultDto> result, OutTaskAlgorithmDto outTaskAlgorithmDto, WholeStationDto wholeStationDto, OutboundStrategyConfigVo outboundStrategyConfigVo) throws Exception {
        this.generateContainerBindTask(result, outTaskAlgorithmDto, wholeStationDto, outboundStrategyConfigVo);
        this.sendCarryTask(result);
    }

    /**
     * 生成容器绑定任务
     */
    private void generateContainerBindTask(List<WholeOutStrategyResultDto> result, OutTaskAlgorithmDto outTaskAlgorithmDto, WholeStationDto wholeStationDto, OutboundStrategyConfigVo outboundStrategyConfigVo) throws Exception {
        List<String> containerList = result.stream().map(p -> p.getContaienrNo()).collect(Collectors.toList());
        RestMessage<List<EisInvContainerStoreVo>> containerStoreListRest = eisContainerStoreFeign.findByContainerNos(containerList);
        if (!containerStoreListRest.isSuccess() || containerStoreListRest.getData().isEmpty()) {
            throw new PrologException(String.format("容器集合[%s]库存查询失败原因[%s]", containerList.toString(), containerStoreListRest.getMessage()));
        }
        List<EisInvContainerStoreVo> eisInvContainerStoreVoList = containerStoreListRest.getData();
        for (WholeOutStrategyResultDto wholeOutStrategyResultDto : result) {
            List<OutboundTaskBind> outboundTaskBindList = outboundTaskBindMapper.findByMap(MapUtils.put("containerNo", wholeOutStrategyResultDto.getContaienrNo()).getMap(), OutboundTaskBind.class);
            if (!outboundTaskBindList.isEmpty()) {
                log.error("容器{}已经生成了绑定任务", wholeOutStrategyResultDto.getContaienrNo());
            } else {
                EisInvContainerStoreVo eisInvContainerStoreVo = eisInvContainerStoreVoList.stream().filter(p -> wholeOutStrategyResultDto.getContaienrNo().equals(p.getContainerNo())).findFirst().orElse(null);
                if (eisInvContainerStoreVo == null) {
                    throw new PrologException(String.format("参数有误,容器[%s]查询不到库存", wholeOutStrategyResultDto.getContaienrNo()));
                }
                OutboundTaskBind outboundTaskBind = new OutboundTaskBind();
                outboundTaskBind.setStationId(wholeStationDto.getStationId());
                outboundTaskBind.setCreateTime(new Date());
                outboundTaskBind.setContainerNo(wholeOutStrategyResultDto.getContaienrNo());
                outboundTaskBindMapper.save(outboundTaskBind);
                List<OutboundTaskBindDetail> outboundTaskBindDetailList = new ArrayList<>();
                for (String containerSub : wholeOutStrategyResultDto.getContainerSubNoList()) {
                    EisInvContainerStoreSubVo eisInvContainerStoreSubVo = eisInvContainerStoreVo.getContainerStoreSubList().stream().filter(p -> containerSub.equals(p.getContainerStoreSubNo())).findFirst().orElse(null);
                    if (eisInvContainerStoreSubVo == null) {
                        throw new PrologException(String.format("参数有误,子容器[%s]查询不到库存", eisInvContainerStoreSubVo.getContainerStoreSubNo()));
                    }
                    List<OutTaskDetailAlgorithmDto> outTaskDetailList = outTaskAlgorithmDto.getOutTaskDetailList();
                    OutTaskDetailAlgorithmDto outTaskDetailAlgorithmDto = null;
                    if (outboundStrategyConfigVo.getStoreMatchingStrategy() == 1) {
                        outTaskDetailAlgorithmDto = outTaskDetailList.stream().filter(p -> eisInvContainerStoreSubVo.getItemId().equals(p.getUniqueKey())).findFirst().orElse(null);
                    } else {
                        outTaskDetailAlgorithmDto = outTaskDetailList.stream().filter(p -> eisInvContainerStoreSubVo.getLotId().equals(p.getUniqueKey())).findFirst().orElse(null);
                    }
                    if (outTaskDetailAlgorithmDto == null) {
                        throw new PrologException("库存明细的唯一键无法匹配出订单");
                    }
                    OutboundTaskBindDetail outboundTaskBindDetail = new OutboundTaskBindDetail();
                    outboundTaskBindDetail.setOutbTaskBindId(outboundTaskBind.getId());
                    outboundTaskBindDetail.setLotId(eisInvContainerStoreSubVo.getLotId());
                    outboundTaskBindDetail.setItemId(eisInvContainerStoreSubVo.getItemId());
                    outboundTaskBindDetail.setCreateTime(new Date());
                    outboundTaskBindDetail.setContainerNo(wholeOutStrategyResultDto.getContaienrNo());
                    outboundTaskBindDetail.setContainerNoSub(eisInvContainerStoreSubVo.getContainerStoreSubNo());
                    outboundTaskBindDetail.setBindingNum(eisInvContainerStoreSubVo.getQty());
                    outboundTaskBindDetail.setOutTaskDetailId(outTaskDetailAlgorithmDto.getOutTaskDetailId());
                    outboundTaskBindDetailList.add(outboundTaskBindDetail);
                }
                outboundTaskBindDetailMapper.saveBatch(outboundTaskBindDetailList);
            }
        }
    }

    /**
     * 生成搬运任务
     */
    private void sendCarryTask(List<WholeOutStrategyResultDto> wholeOutStrategyResultDtos) throws Exception {
        List<String> containerList = wholeOutStrategyResultDtos.stream().map(p -> p.getContaienrNo()).collect(Collectors.toList());
//        CarryTask carryInterfaceTask = new CarryTask();
//        carryInterfaceTask.setId(PrologStringUtils.newGUID());
//        carryInterfaceTask.setContainerNo(containerTaskDetail.getContainerNo());
//        carryInterfaceTask.setTaskType(20);
//        carryInterfaceTask.setStartLocation(containerTaskDetail.getSourceArea());
//        carryInterfaceTask.setStartLocation(containerTaskDetail.getSourceLocation());
//        carryInterfaceTask.setEndRegion(containerTaskDetail.getTargetArea());
//        carryInterfaceTask.setEndLocation(containerTaskDetail.getTargetLocation());
//        carryInterfaceTask.setPriority(containerTask.getPriority() != 0 ? containerTask.getPriority() : containerTaskStrategy.getPriority());
//        String json = JSONObject.toJSONString(carryInterfaceTask);
//        RestMessage<String> carry = eisContainerRouteClient.createCarry(json);
    }
}
