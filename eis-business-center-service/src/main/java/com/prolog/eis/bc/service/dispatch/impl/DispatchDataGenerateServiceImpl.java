package com.prolog.eis.bc.service.dispatch.impl;

import com.prolog.eis.bc.dao.OutboundTaskBindDetailMapper;
import com.prolog.eis.bc.dao.OutboundTaskBindMapper;
import com.prolog.eis.bc.dao.OutboundTaskMapper;
import com.prolog.eis.bc.facade.dto.outbound.WholeStationDto;
import com.prolog.eis.bc.facade.vo.OutboundStrategyConfigVo;
import com.prolog.eis.bc.feign.container.EisContainerRouteClient;
import com.prolog.eis.bc.feign.container.EisContainerStoreFeign;
import com.prolog.eis.bc.service.dispatch.DispatchDataGenerateService;
import com.prolog.eis.common.util.PrologStringUtils;
import com.prolog.eis.core.model.biz.carry.CarryTask;
import com.prolog.eis.core.model.biz.outbound.OutboundTask;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskBind;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskBindDetail;
import com.prolog.eis.router.vo.ContainerLocationVo;
import com.prolog.framework.common.message.RestMessage;
import com.prolog.framework.core.exception.PrologException;
import com.prolog.framework.utils.MapUtils;
import com.prolog.upcloud.base.inventory.vo.EisInvContainerStoreSubVo;
import com.prolog.upcloud.base.inventory.vo.EisInvContainerStoreVo;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.OutTaskAlgorithmDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.OutTaskDetailAlgorithmDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.WholeOutStrategyResultDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/10/27
 **/
@Service
@Slf4j
public class DispatchDataGenerateServiceImpl implements DispatchDataGenerateService {

    @Autowired
    private EisContainerRouteClient eisContainerRouteClient;
    @Autowired
    private OutboundTaskBindMapper outboundTaskBindMapper;
    @Autowired
    private EisContainerStoreFeign eisContainerStoreFeign;
    @Autowired
    private OutboundTaskBindDetailMapper outboundTaskBindDetailMapper;
    @Autowired
    private OutboundTaskMapper outboundTaskMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void generateData(List<WholeOutStrategyResultDto> result, OutTaskAlgorithmDto outTaskAlgorithmDto, WholeStationDto wholeStationDto, OutboundStrategyConfigVo outboundStrategyConfigVo) throws Exception {
        this.generateContainerBindTask(result, outTaskAlgorithmDto, wholeStationDto, outboundStrategyConfigVo);
        this.sendCarryTask(result, wholeStationDto);
    }

    /**
     * 生成容器绑定任务
     */
    private void generateContainerBindTask(List<WholeOutStrategyResultDto> result, OutTaskAlgorithmDto outTaskAlgorithmDto, WholeStationDto wholeStationDto, OutboundStrategyConfigVo outboundStrategyConfigVo) throws Exception {
        List<String> containerList = result.stream().map(p -> p.getContaienrNo()).collect(Collectors.toList());
        RestMessage<List<EisInvContainerStoreVo>> containerStoreListRest = eisContainerStoreFeign.findByContainerNos(containerList);
        if (!containerStoreListRest.isSuccess() || containerStoreListRest.getData() == null) {
            throw new PrologException(String.format("容器集合[%s]库存查询失败原因[%s]", containerList.toString(), containerStoreListRest.getMessage()));
        }
        outboundTaskMapper.updateMapById(outTaskAlgorithmDto.getOutTaskId(), MapUtils.put("state", 1).getMap(), OutboundTask.class);
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
    private void sendCarryTask(List<WholeOutStrategyResultDto> wholeOutStrategyResultDtos, WholeStationDto wholeStationDto) throws Exception {
        List<String> containerList = wholeOutStrategyResultDtos.stream().map(p -> p.getContaienrNo()).collect(Collectors.toList());

        /**
         * 查询容器位置
         */
        RestMessage<List<ContainerLocationVo>> locationByContainerListRest = eisContainerRouteClient.findLocationByContainerList(containerList);
        if (!locationByContainerListRest.isSuccess() || locationByContainerListRest.getData() == null) {
            throw new PrologException("请求容器位置失败");
        }
        List<ContainerLocationVo> containerLocationVoList = locationByContainerListRest.getData();
        List<CarryTask> carryTaskList = new ArrayList<>();
        for (String containerNo : containerList) {
            ContainerLocationVo containerLocationVo = containerLocationVoList.stream().filter(p -> containerNo.equals(p.getContainerNo())).findFirst().orElse(null);
            if (containerLocationVo == null) {
                throw new PrologException(String.format("容器号[%s]无法查询到位置,严重异常,请检查数据", containerNo));
            }
            CarryTask carryInterfaceTask = new CarryTask();
            carryInterfaceTask.setId(PrologStringUtils.newGUID());
            carryInterfaceTask.setContainerNo(containerNo);
            carryInterfaceTask.setTaskType(30);
            carryInterfaceTask.setStartLocation(containerLocationVo.getSourceArea());
            carryInterfaceTask.setStartLocation(containerLocationVo.getSourceLocation());
            carryInterfaceTask.setEndRegion(wholeStationDto.getAreaNo());
            carryInterfaceTask.setPriority(50);
            carryTaskList.add(carryInterfaceTask);

        }
        RestMessage<String> carry = eisContainerRouteClient.createBatchCarry(carryTaskList);
        if (carry.isSuccess()) {
            throw new PrologException("生成搬运任务失败,请检查路径服务");
        }
    }
}
