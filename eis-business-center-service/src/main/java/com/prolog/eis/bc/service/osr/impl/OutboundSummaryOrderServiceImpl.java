package com.prolog.eis.bc.service.osr.impl;

import com.prolog.eis.bc.dao.*;
import com.prolog.eis.bc.facade.dto.osr.OutSummaryOrderInfoDto;
import com.prolog.eis.bc.facade.dto.osr.SplitStrategyResultDto;
import com.prolog.eis.bc.feign.container.EisContainerLocationFeign;
import com.prolog.eis.bc.feign.container.EisContainerStoreFeign;
import com.prolog.eis.bc.service.osr.OutboundSummaryOrderService;
import com.prolog.eis.bc.service.osr.SplitStrategy;
import com.prolog.eis.bc.service.ssc.OutboundSplitStrategyConfigService;
import com.prolog.eis.bc.service.sscdtl.OutboundSplitStrategyDetailConfigService;
import com.prolog.eis.core.model.biz.outbound.*;
import com.prolog.eis.core.model.biz.route.ContainerLocation;
import com.prolog.eis.core.model.ctrl.outbound.OutboundSplitStrategyConfig;
import com.prolog.eis.core.model.ctrl.outbound.OutboundSplitStrategyDetailConfig;
import com.prolog.upcloud.base.inventory.vo.EisInvContainerStoreSubVo;
import com.prolog.upcloud.base.inventory.vo.EisInvContainerStoreVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: txz
 * @Date: 2021/9/23 15:49
 * @Desc: 出库任务汇总单service
 */
@Service
public class OutboundSummaryOrderServiceImpl implements OutboundSummaryOrderService {

    @Autowired
    private OutboundSplitStrategyConfigService splitStrategyConfigService;
    @Autowired
    private OutboundSplitStrategyDetailConfigService splitStrategyDetailService;
    @Autowired
    private EisContainerLocationFeign containerLocationFeign;
    @Autowired
    private EisContainerStoreFeign storeFeign;
    @Autowired
    private OutboundSummaryOrderMapper mapper;
    @Autowired
    private OutboundTaskMapper taskMapper;
    @Autowired
    private OutboundTaskDetailMapper taskDetailMapper;
    @Autowired
    private OutboundSummaryOrderHisMapper hisMapper;
    @Autowired
    private OutboundTaskHisMapper taskHisMapper;
    @Autowired
    private OutboundTaskDetailHisMapper taskDetailHisMapper;

    @Autowired
    private OutboundTaskBindMapper taskBindMapper;
    @Autowired
    private OutboundTaskBindDetailMapper taskBindDetailMapper;
    @Autowired
    private OutboundTaskBindHisMapper taskBindHisMapper;
    @Autowired
    private OutboundTaskBindDetailHisMapper taskBindDetailHisMapper;

    private static final int Strategy_All = 1;//整拖
    private static final int Strategy_Lfz = 2;//零发整

    @Override
    @Transactional
    public String createOutOrder(OutSummaryOrderInfoDto dto) throws Exception{
        //1.根据订单类型查找对应拆单策略及明细
        OutboundSplitStrategyConfig strategy = splitStrategyConfigService.getByStrategyTypeNo(dto.getOrderType());
        List<OutboundSplitStrategyDetailConfig> strategyDtls = splitStrategyDetailService.getDtlsByOutSplitStgCfgId(strategy.getId()).stream().sorted(Comparator.comparing(OutboundSplitStrategyDetailConfig::getSortIndex)).collect(Collectors.toList());
        //2.根据拆单策略明细中的区域编号去找区域中的容器编号
        List<ContainerLocation> locationList = new LinkedList<>();
        for (OutboundSplitStrategyDetailConfig strategyDtl : strategyDtls) {
//            strategyDtl.getSortIndex()
            List<ContainerLocation> byAreaNo = containerLocationFeign.findByAreaNo("sourceArea",strategyDtl.getAreaNo()).getData();
            locationList.addAll(byAreaNo);
        }
        Assert.notEmpty(locationList,"未找到容器位置信息");

        //3.根据容器编号去统计子容器可装载库存数量
        List<EisInvContainerStoreVo> containerS = new LinkedList<>(); //容器
        for (ContainerLocation containerLocation : locationList) {
            List<EisInvContainerStoreVo> data = storeFeign.findByContainerNo(containerLocation.getContainerNo()).getData();
            containerS.add((EisInvContainerStoreVo) data);
        }
        Assert.notEmpty(containerS,"未找到容器库存信息");

        //4.根据策略排序查找最适合单据数量的容器，一般遵循整托，零发整，拆零的优先策略
        double orderQty = dto.getDtls().stream().collect(Collectors.summingDouble(x -> x.getOrderQty()));
        List<SplitStrategyResultDto> tis = new ArrayList<>();
        for (OutboundSplitStrategyDetailConfig strategyDtl : strategyDtls) {
            if(strategyDtl.getOutSplitStgCfgId().equals(Strategy_All)){
                SplitStrategyResultDto resultDto = SplitStrategy.zhengTuoStrategy(orderQty, containerS);
                tis.add(resultDto);
            }if(strategyDtl.getOutSplitStgCfgId().equals(Strategy_Lfz)){
                SplitStrategyResultDto resultDto = SplitStrategy.pinXiangStrategy(orderQty, containerS);
                tis.add(resultDto);
            }
        }
        //5.生成任务汇总单，任务单表，任务单表明细
        String summaryId = saveData(dto, tis);
        return "创建成功,汇总单号: "+summaryId;
    }

    /**
     * 数据存储
     * @param dto
     * @param tis
     * @return
     */
    public String saveData(OutSummaryOrderInfoDto dto,List<SplitStrategyResultDto> tis){

        /** 任务汇总单与历史 **/
        OutboundSummaryOrder summaryOrder = new OutboundSummaryOrder();
        summaryOrder.setTypeNo(dto.getOrderType());
        summaryOrder.setState("未开始");
        summaryOrder.setCreateTime(new Date());

        OutboundSummaryOrderHis summaryOrderHis = new OutboundSummaryOrderHis();
        BeanUtils.copyProperties(summaryOrder,summaryOrderHis);

        long id = mapper.save(summaryOrder);
        hisMapper.save(summaryOrderHis);

        Date date = new Date();
        for (OutSummaryOrderInfoDto.OutSummaryOrderDetailInfoDto dtl : dto.getDtls()) {

            /** 任务单表与历史 **/
            OutboundTask task = new OutboundTask();
            task.setOutTaskSmyId(String.valueOf(id));
            task.setOutboundTaskTypeNo(dto.getOrderType());
            task.setState(0);
            task.setIsShortPicking(0);
            task.setPriority(0);
            //task.setExpireDate(date);
            task.setCreateTime(date);
            //task.setEnterpriseId("");
            //task.setCargoOwnerId("");
            //task.setWarehouseId("");

            OutboundTaskHis taskHis = new OutboundTaskHis();
            BeanUtils.copyProperties(task,taskHis);

            long taskId = taskMapper.save(task);
            taskHisMapper.save(taskHis);

            //List<OutboundTaskDetail> taskDetails = new LinkedList<>();
            for (SplitStrategyResultDto ti : tis) {

                /** 任务单表明细与历史 **/
                OutboundTaskDetail taskDetail = new OutboundTaskDetail();
                taskDetail.setOutTaskId(String.valueOf(taskId));
                taskDetail.setLotId(dtl.getLotId());
                taskDetail.setItemId(dtl.getItemId());
                taskDetail.setPlanNum(dtl.getOrderQty().floatValue());
                taskDetail.setActualNum(dtl.getOrderQty().floatValue());
                taskDetail.setIsFinish(0);
                taskDetail.setIsShortPicking(0);
                taskDetail.setCreateTime(date);

                OutboundTaskDetailHis taskDetailHis = new OutboundTaskDetailHis();
                BeanUtils.copyProperties(taskDetail,taskDetailHis);


                long taskDetailId = taskDetailMapper.save(taskDetail);
                taskDetailHisMapper.save(taskDetailHis);


                for (int i = 0;i<ti.getContainerNos().size();i++) {
                    /** 生成容器绑定与历史 **/
                    String containerNo = ti.getContainerNos().get(i);
                    OutboundTaskBind bind = new OutboundTaskBind();
                    bind.setContainerNo(containerNo);
                    //bind.setPickingOrderId("");
                    //bind.setStationId("");
                    //bind.setOrderPoolId("");
                    bind.setCreateTime(date);
                    //bind.setFinishTime(new Date());
                    //bind.setEnterpriseId("");
                    //bind.setCargoOwnerId("");
                    //bind.setWarehouseId("");

                    OutboundTaskBindHis bindHis = new OutboundTaskBindHis();
                    BeanUtils.copyProperties(bind,bindHis);

                    long taskBindId = taskBindMapper.save(bind);
                    taskBindHisMapper.save(bindHis);

                    List<OutboundTaskBindDetail> bindDetails = new LinkedList<>();
                    List<OutboundTaskBindDetailHis> bindDetailHiss = new LinkedList<>();
                    for (int j = 0; j < ti.getSubContainerNos().size(); j++) {
                        List<String> strings = ti.getSubContainerNos().get(j);
                        List<Double> doubles = ti.getSubConQtys().get(j);

                        for (int k = 0; k < strings.size(); k++) {
                            String subNo = strings.get(k);
                            Double subQty = doubles.get(k);

                            /** 生成容器绑定明细与历史 **/
                            OutboundTaskBindDetail bindDetail = new OutboundTaskBindDetail();
                            bindDetail.setOutbTaskBindId(taskBindId+"");
                            bindDetail.setContainerNo(containerNo);
                            bindDetail.setContainerNoSub(subNo);
                            bindDetail.setOutTaskDetailId(taskDetailId+"");
                            bindDetail.setItemId(dtl.getItemId());
                            bindDetail.setLotId(dtl.getLotId());
                            bindDetail.setBindingNum(subQty.floatValue());
                            bindDetail.setCreateTime(date);
                            //bindDetail.setFinishTime(new Date());
                            //bindDetail.setEnterpriseId("");
                            //bindDetail.setCargoOwnerId("");
                            //bindDetail.setWarehouseId("");

                            OutboundTaskBindDetailHis bindDetailHis = new OutboundTaskBindDetailHis();
                            BeanUtils.copyProperties(bindDetail,bindDetailHis);

                            bindDetails.add(bindDetail);
                            bindDetailHiss.add(bindDetailHis);
                        }
                    }
                   taskBindDetailMapper.saveBatch(bindDetails);
                   taskBindDetailHisMapper.saveBatch(bindDetailHiss);
                }
            }
        }

        return summaryOrder.getId();
    }
}