package com.prolog.eis.bc.service.osr.impl;
import com.alibaba.fastjson.JSONObject;
import com.prolog.eis.bc.dao.*;
import com.prolog.eis.bc.facade.dto.businesscenter.OutboundSummaryOrderDto;
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
import com.prolog.framework.core.pojo.Page;
import com.prolog.framework.core.restriction.Criteria;
import com.prolog.framework.core.restriction.Restriction;
import com.prolog.framework.core.restriction.Restrictions;
import com.prolog.framework.dao.util.PageUtils;
import com.prolog.upcloud.base.inventory.vo.EisInvContainerStoreVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: txz
 * @Date: 2021/9/23 15:49
 * @Desc: 出库任务汇总单service
 *  出库汇总任务单
 *  * 1.出库汇总任务单任务下发
 *  * 2.出库汇总任务单任务回告完成
 *  * 3.拆单调度
 */
@Service
@Slf4j
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
    @Autowired
    private OutboundTaskReportMapper reportMapper;

    private static final String Strategy_All = "All";//整拖
    private static final String Strategy_Lfz = "Px";//零发整

    @Override
    @Transactional
    public String createOutOrder(OutSummaryOrderInfoDto dto) throws Exception{
        //1.根据订单类型查找对应拆单策略及明细
        OutboundSplitStrategyConfig strategy = splitStrategyConfigService.getByStrategyTypeNo(dto.getOrderType());
        List<OutboundSplitStrategyDetailConfig> strategyDtls = splitStrategyDetailService.getDtlsByOutSplitStgCfgId(strategy.getId()).stream().sorted(Comparator.comparing(OutboundSplitStrategyDetailConfig::getSortIndex)).collect(Collectors.toList());
        Assert.notEmpty(strategyDtls,"未找到拆单策略信息");

        //2.根据拆单策略明细中的区域编号去找区域中的容器编号
        List<ContainerLocation> locationList = new LinkedList<>();
        for (OutboundSplitStrategyDetailConfig strategyDtl : strategyDtls) {
            JSONObject json = new JSONObject();
            json.put("areaKey","sourceArea");
            json.put("keyValue",strategyDtl.getAreaNo());
            try {
                List<ContainerLocation> cls  = containerLocationFeign.findByAreaNo(json.toJSONString()).getData();
                locationList.addAll(cls);
            } catch (Exception e) {
                e.printStackTrace();
                log.info(json.toJSONString());
                log.error("containerLocationFeign -> findByAreaNo : %s",e.getMessage());
            }
        }
        Assert.notEmpty(locationList,"未找到容器位置信息");

        //3.根据容器编号去统计子容器可装载库存数量
        List<String> containerNoS = locationList.stream().filter(s -> s.getTaskState() == 0 && s.getTaskType() == 0).map(ContainerLocation::getContainerNo).distinct().collect(Collectors.toList());
        List<EisInvContainerStoreVo> containerStoreS = null;
        try {
            containerStoreS = storeFeign.findByContainerNos(containerNoS).getData();
        } catch (Exception e) {
            e.printStackTrace();
            log.info(Arrays.asList(containerNoS).toString());
            log.error("storeFeign -> findByContainerNos : %s",e.getMessage());
        }
        Assert.notEmpty(containerStoreS,"未找到容器库存信息");
        containerStoreS = containerStoreS.stream().filter(d -> d.getTaskType() == 0).collect(Collectors.toList());

        //4.根据策略排序查找最适合单据数量的容器，一般遵循整托，零发整，拆零的优先策略
        double orderQty = dto.getDtls().stream().collect(Collectors.summingDouble(x -> x.getOrderQty()));
        List<SplitStrategyResultDto> splitResults = new ArrayList<>();
        for (OutboundSplitStrategyDetailConfig strategyDtl : strategyDtls) {
            if (orderQty > 0) {
                SplitStrategyResultDto resultDto = null;
                if(strategyDtl.getSplitStrategy().equals(Strategy_All)){
                    resultDto = SplitStrategy.zhengTuoStrategy(orderQty, containerStoreS);
                }else if(strategyDtl.getSplitStrategy().equals(Strategy_Lfz)){
                    resultDto = SplitStrategy.pinXiangStrategy(orderQty, containerStoreS);
                }
                orderQty = resultDto.getRemainOrderQty();
                splitResults.add(resultDto);
            }
        }

        //5.生成任务汇总单，任务单表，任务单表明细
        String summaryId = saveData(dto, splitResults);
        return "创建成功,汇总单号: "+summaryId;
    }

    /**
     * 数据存储(出库汇总单，任务单表及明细，容器绑定及明细，任务单回告)
     * @param orderDto 上游外来订单
     * @param splitResults 拆单结果
     * @return
     */
    public String saveData(OutSummaryOrderInfoDto orderDto,List<SplitStrategyResultDto> splitResults){

        /** 任务汇总单与历史 **/
        Date date = new Date();
        OutboundSummaryOrder summaryOrder = new OutboundSummaryOrder();

        summaryOrder.setTypeNo(orderDto.getOrderType());
        summaryOrder.setState("未开始");
        summaryOrder.setCreateTime(date);

        //OutboundSummaryOrderHis summaryOrderHis = new OutboundSummaryOrderHis();
        //BeanUtils.copyProperties(summaryOrder,summaryOrderHis);

        long id = mapper.save(summaryOrder);
        //hisMapper.save(summaryOrderHis);

        for (OutSummaryOrderInfoDto.OutSummaryOrderDetailInfoDto orderDtl : orderDto.getDtls()) {

            /** 任务单表与历史 **/
            OutboundTask task = new OutboundTask();
            task.setOutTaskSmyId(String.valueOf(id));
            task.setOutboundTaskTypeNo(orderDto.getOrderType());
            task.setState(0);
            task.setIsShortPicking(0);
            task.setPriority(0);
            //task.setExpireDate(date);
            task.setCreateTime(date);
            //task.setEnterpriseId("");
            //task.setCargoOwnerId("");
            //task.setWarehouseId("");

            //OutboundTaskHis taskHis = new OutboundTaskHis();
            //BeanUtils.copyProperties(task,taskHis);

            long taskId = taskMapper.save(task);
            //taskHisMapper.save(taskHis);

            /** 容器任务单回告 **/
            OutboundTaskReport taskReport = new OutboundTaskReport();
            taskReport.setOutTaskId(taskId+"");
            taskReport.setOutboundTaskTypeNo(orderDto.getOrderType());
            //taskReport.setUpperSystemTaskId("");
            taskReport.setCreateTime(date);
            //taskReport.setEnterpriseId("");
            //taskReport.setCargoOwnerId("");
            //taskReport.setWarehouseId("");
            reportMapper.save(taskReport);

            //List<OutboundTaskDetail> taskDetails = new LinkedList<>();
            for (SplitStrategyResultDto resultDto : splitResults) {
                for (int i = 0;i<resultDto.getContainerNos().size();i++) {
                    /** 生成容器绑定与历史 **/
                    String containerNo = resultDto.getContainerNos().get(i);
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

                    //OutboundTaskBindHis bindHis = new OutboundTaskBindHis();
                    //BeanUtils.copyProperties(bind,bindHis);

                    long taskBindId = taskBindMapper.save(bind);
                    //taskBindHisMapper.save(bindHis);

                    List<OutboundTaskBindDetail> bindDetails = new LinkedList<>();
                    //List<OutboundTaskBindDetailHis> bindDetailHiss = new LinkedList<>();
                    for (int j = 0; j < resultDto.getSubContainerNos().size(); j++) {
                        List<String> subNos = resultDto.getSubContainerNos().get(j);
                        List<Double> subQtys = resultDto.getSubConQtys().get(j);

                        for (int k = 0; k < subNos.size(); k++) {
                            String subNo = subNos.get(k);
                            Double subQty = subQtys.get(k);

                            /** 任务单表明细与历史 **/
                            OutboundTaskDetail taskDetail = new OutboundTaskDetail();
                            taskDetail.setOutTaskId(String.valueOf(taskId));
                            taskDetail.setLotId(orderDtl.getLotId());
                            taskDetail.setItemId(orderDtl.getItemId());
                            taskDetail.setPlanNum(subQty.floatValue());
                            taskDetail.setActualNum(subQty.floatValue());
                            taskDetail.setIsFinish(0);
                            taskDetail.setIsShortPicking(0);
                            taskDetail.setCreateTime(date);

                            //OutboundTaskDetailHis taskDetailHis = new OutboundTaskDetailHis();
                            //BeanUtils.copyProperties(taskDetail,taskDetailHis);


                            long taskDetailId = taskDetailMapper.save(taskDetail);
                            //taskDetailHisMapper.save(taskDetailHis);

                            /** 生成容器绑定明细与历史 **/
                            OutboundTaskBindDetail bindDetail = new OutboundTaskBindDetail();
                            bindDetail.setOutbTaskBindId(taskBindId+"");
                            bindDetail.setContainerNo(containerNo);
                            bindDetail.setContainerNoSub(subNo);
                            bindDetail.setOutTaskDetailId(taskDetailId+"");
                            bindDetail.setItemId(orderDtl.getItemId());
                            bindDetail.setLotId(orderDtl.getLotId());
                            bindDetail.setBindingNum(subQty.floatValue());
                            bindDetail.setCreateTime(date);
                            //bindDetail.setFinishTime(new Date());
                            //bindDetail.setEnterpriseId("");
                            //bindDetail.setCargoOwnerId("");
                            //bindDetail.setWarehouseId("");

                            //OutboundTaskBindDetailHis bindDetailHis = new OutboundTaskBindDetailHis();
                            //BeanUtils.copyProperties(bindDetail,bindDetailHis);

                            bindDetails.add(bindDetail);
                            //bindDetailHiss.add(bindDetailHis);
                        }
                    }
                   taskBindDetailMapper.saveBatch(bindDetails);
                   //taskBindDetailHisMapper.saveBatch(bindDetailHiss);
                }
            }
        }

        return summaryOrder.getId();
    }

    @Override
    public Page<OutboundSummaryOrder> getutboundSummaryOrderPage(OutboundSummaryOrderDto dto){
        PageUtils.startPage(dto.getPageNum(), dto.getPageSize());
        Criteria criteria = new Criteria(OutboundSummaryOrder.class);
        Restriction r1 = null;
        Restriction r2 = null;
        Restriction r3 = null;
        Restriction r4 = null;

        if (!StringUtils.isEmpty(dto.getTypeNo())) {
            r1 = Restrictions.eq("typeNo", dto.getTypeNo());
        }
        if (!StringUtils.isEmpty(dto.getState())) {
            r2 = Restrictions.eq("state", dto.getState());
        }

        if (dto.getCreateTimeFrom() != null) {
            r3 = Restrictions.ge("createTime", dto.getCreateTimeFrom());
        }
        if (dto.getCreateTimeTo() != null) {
            r4 = Restrictions.le("createTime", dto.getCreateTimeTo());
        }

        criteria.setRestriction(Restrictions.and(r1,r2,r3,r4));
        List<OutboundSummaryOrder> list = mapper.findByCriteria(criteria);
        return PageUtils.getPage(list);
    }
}
