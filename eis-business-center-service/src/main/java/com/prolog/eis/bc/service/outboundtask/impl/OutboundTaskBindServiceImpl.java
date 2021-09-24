package com.prolog.eis.bc.service.outboundtask.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.prolog.eis.bc.constant.OutboundStrategyConfigConstant;
import com.prolog.eis.bc.dao.OutboundTaskBindDtMapper;
import com.prolog.eis.bc.dao.OutboundTaskBindMapper;
import com.prolog.eis.bc.feign.EisInvContainerStoreSubFeign;
import com.prolog.eis.bc.service.outboundtask.OutboundTaskBindService;
import com.prolog.eis.common.util.MathHelper;
import com.prolog.eis.component.algorithm.composeorder.entity.ContainerDto;
import com.prolog.eis.component.algorithm.composeorder.entity.ContainerSubDto;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskBind;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskBindDetail;
import com.prolog.framework.common.message.RestMessage;
import com.prolog.framework.core.restriction.Criteria;
import com.prolog.framework.core.restriction.Restrictions;
import com.prolog.upcloud.base.inventory.vo.EisInvContainerStoreSubVo;
import com.prolog.upcloud.base.inventory.vo.EisInvContainerStoreVo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OutboundTaskBindServiceImpl implements OutboundTaskBindService {

    @Autowired
    private OutboundTaskBindMapper outboundTaskBindMapper;

    @Autowired
    private OutboundTaskBindDtMapper outboundTaskBindDtMapper;

    @Autowired
    private EisInvContainerStoreSubFeign eisInvContainerStoreSubFeign;

    @Override
    public List<ContainerDto> findByPickingOrderIdList(
            List<String> pickingOrderIdList, int storeMatchingStrategy) {
        if (CollectionUtils.isEmpty(pickingOrderIdList)) {
            return Lists.newArrayList();
        }

        // 查询拣货单关联的出货任务绑定信息
        Criteria taskBindCrt = Criteria.forClass(OutboundTaskBind.class);
        taskBindCrt.setRestriction(Restrictions.in("pickingOrderId", pickingOrderIdList.toArray()));
        List<OutboundTaskBind> outboundTaskBindList = outboundTaskBindMapper.findByCriteria(taskBindCrt);
        if (CollectionUtils.isEmpty(outboundTaskBindList)) {
            return Lists.newArrayList();
        }

        // 查询出货绑定信息 关联的 绑定明细
        List<String> outboundTaskBindIdList = outboundTaskBindList.stream().map(OutboundTaskBind::getId).collect(Collectors.toList());
        Criteria taskBindDtCrt = Criteria.forClass(OutboundTaskBindDetail.class);
        taskBindDtCrt.setRestriction(Restrictions.in("outbTaskBindId", outboundTaskBindIdList.toArray()));
        List<OutboundTaskBindDetail> outboundTaskBindDtList = outboundTaskBindDtMapper.findByCriteria(taskBindDtCrt);

        List<String> containerNoList = outboundTaskBindList.stream().map(OutboundTaskBind::getContainerNo).collect(Collectors.toList());
        String containerNo = MathHelper.strListToStr(containerNoList, ",");
        // 查询关联容器信息
        List<EisInvContainerStoreVo> containerStoreList = null;
        RestMessage<List<EisInvContainerStoreVo>> containerStoreResp = null;
        try {
            containerStoreResp = eisInvContainerStoreSubFeign.findByContainerNo(containerNo);
        } catch (Exception e) {
            log.error("eisInvContainerStoreSubFeign.findByContainerNo({}) excp:{}", containerNo, e.getMessage());
        }
        if (null != containerStoreResp && containerStoreResp.isSuccess()) {
            containerStoreList = containerStoreResp.getData();
        } else {
            log.error("eisInvContainerStoreSubFeign.findByContainerNo({}) return error, msg:{}",
                    containerNo, null == containerStoreResp ? "resp is null" : containerStoreResp.getMessage());
            containerStoreList = Lists.newArrayList();
        }

        
        
        List<ContainerDto> resultList = Lists.newArrayList();
        for (OutboundTaskBind outboundTaskBind : outboundTaskBindList) {
            ContainerDto containerDto = new ContainerDto();
            containerDto.setContainerNo(outboundTaskBind.getContainerNo());
            containerDto.setPickingOrderId(outboundTaskBind.getPickingOrderId());
            containerDto.setStationId(outboundTaskBind.getStationId());

            // 根据本地容器No匹配关联远程数据,远程数据中包含容器商品数量，与本地绑定数量进行计算
            EisInvContainerStoreVo relaContainerStore = null;
            for (EisInvContainerStoreVo containerStore : containerStoreList) {
                if (null != outboundTaskBind.getContainerNo() && outboundTaskBind.getContainerNo().equals(containerStore.getContainerNo())) {
                    relaContainerStore = containerStore;
                    break;
                }
            }
            // 远程容器子容器数据，包含商品数量，商品Id，批次Id
            List<EisInvContainerStoreSubVo> relaContainerStoreSubList = null;
            if (null != relaContainerStore) {
                relaContainerStoreSubList = relaContainerStore.getContainerStoreSubList();
            }
            
            // 匹配子容器数据
            List<ContainerSubDto> containerSubDtoList = Lists.newArrayList();
            for (OutboundTaskBindDetail outboundTaskBindDetail : outboundTaskBindDtList) {
                if (null != outboundTaskBind.getId() && outboundTaskBind.getId().equals(outboundTaskBindDetail.getOutbTaskBindId())) {
                    ContainerSubDto containerSubDto = new ContainerSubDto();
                    containerSubDto.setContainerNo(outboundTaskBindDetail.getContainerNo());
                    containerSubDto.setContainerSubNo(outboundTaskBindDetail.getContainerNoSub());

                    if (storeMatchingStrategy == OutboundStrategyConfigConstant.STORE_MATCHING_STRATEGY_IOT) {
                        // 按批次出库
                        containerSubDto.setItemId(outboundTaskBindDetail.getLotId());
                    } else if (storeMatchingStrategy == OutboundStrategyConfigConstant.STORE_MATCHING_STRATEGY_ITEM) {
                        // 按商品出库
                        containerSubDto.setItemId(outboundTaskBindDetail.getItemId());
                    }
                    // 容器->商品总数量
                    containerSubDto.setItemNum(0);
                    // 容器->子容器绑定的商品数量
                    containerSubDto.setContainerAndOutDetailBindingMap(null);
                    containerSubDto.setItemNum(outboundTaskBindDetail.getBindingNum());
                    containerSubDtoList.add(containerSubDto);
                }
            }
            containerDto.setContainerSubList(containerSubDtoList);
            resultList.add(containerDto);
        }
        return resultList;
    }

}
