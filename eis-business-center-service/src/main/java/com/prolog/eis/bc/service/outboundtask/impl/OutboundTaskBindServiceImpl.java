package com.prolog.eis.bc.service.outboundtask.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.prolog.eis.bc.dao.OutboundTaskBindDetailMapper;
import com.prolog.eis.bc.facade.vo.OutboundTaskBindVo;
import com.prolog.framework.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.prolog.eis.bc.constant.OutboundStrategyConfigConstant;
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
    private OutboundTaskBindDetailMapper outboundTaskBindDtMapper;

    @Autowired
    private EisInvContainerStoreSubFeign eisInvContainerStoreSubFeign;

    @Override
    public List<ContainerDto> findByPickingOrderIdList(
            List<String> pickingOrderIdList, int storeMatchingStrategy) {
        if (CollectionUtils.isEmpty(pickingOrderIdList)) {
            return Lists.newArrayList();
        }

        // ????????????????????????????????????????????????
        Criteria taskBindCrt = Criteria.forClass(OutboundTaskBind.class);
        taskBindCrt.setRestriction(Restrictions.in("pickingOrderId", pickingOrderIdList.toArray()));
        List<OutboundTaskBind> outboundTaskBindList = outboundTaskBindMapper.findByCriteria(taskBindCrt);
        if (CollectionUtils.isEmpty(outboundTaskBindList)) {
            return Lists.newArrayList();
        }

        // ???????????????????????? ????????? ????????????
        List<String> outboundTaskBindIdList = outboundTaskBindList.stream().map(OutboundTaskBind::getId).collect(Collectors.toList());
        Criteria taskBindDtCrt = Criteria.forClass(OutboundTaskBindDetail.class);
        taskBindDtCrt.setRestriction(Restrictions.in("outbTaskBindId", outboundTaskBindIdList.toArray()));
        List<OutboundTaskBindDetail> outboundTaskBindDtList = outboundTaskBindDtMapper.findByCriteria(taskBindDtCrt);

        List<String> containerNoList = outboundTaskBindList.stream().map(OutboundTaskBind::getContainerNo).collect(Collectors.toList());
        String containerNo = MathHelper.strListToStr(containerNoList, ",");
        // ????????????????????????
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
            // outbound????????????->??????Dto??????
            ContainerDto containerDto = new ContainerDto();
            containerDto.setContainerNo(outboundTaskBind.getContainerNo());
            containerDto.setPickingOrderId(outboundTaskBind.getPickingOrderId());
            containerDto.setStationId(outboundTaskBind.getStationId());

            // ??????????????????No????????????????????????,???????????????????????????????????????????????????????????????????????????
            EisInvContainerStoreVo relaContainerStore = null;
            for (EisInvContainerStoreVo containerStore : containerStoreList) {
                if (null != outboundTaskBind.getContainerNo() && outboundTaskBind.getContainerNo().equals(containerStore.getContainerNo())) {
                    relaContainerStore = containerStore;
                    break;
                }
            }
            // ?????????????????????????????????????????????????????????Id?????????Id
            List<EisInvContainerStoreSubVo> relaContainerStoreSubList = null;
            if (null != relaContainerStore) {
                relaContainerStoreSubList = relaContainerStore.getContainerStoreSubList();
            }
            relaContainerStoreSubList = null == relaContainerStoreSubList ? Lists.newArrayList() : relaContainerStoreSubList;
            // ?????????????????????
            List<ContainerSubDto> containerSubDtoList = Lists.newArrayList();
            for (EisInvContainerStoreSubVo eisInvContainerStoreSubVo : relaContainerStoreSubList) {
                ContainerSubDto containerSubDto = new ContainerSubDto();
                containerSubDto.setContainerNo(containerDto.getContainerNo());
                containerSubDto.setContainerSubNo(eisInvContainerStoreSubVo.getContainerStoreSubNo());
                if (storeMatchingStrategy == OutboundStrategyConfigConstant.STORE_MATCHING_STRATEGY_IOT) {
                    // ???????????????
                    containerSubDto.setItemId(eisInvContainerStoreSubVo.getLotId());
                } else if (storeMatchingStrategy == OutboundStrategyConfigConstant.STORE_MATCHING_STRATEGY_ITEM) {
                    // ???????????????
                    containerSubDto.setItemId(eisInvContainerStoreSubVo.getItemId());
                }
                containerSubDto.setItemNum((float) eisInvContainerStoreSubVo.getQty());
                // ???????????????bindingdt?????????
                List<OutboundTaskBindDetail> relaOutboundTaskBindDetailList = 
                        getOutboundTaskBindDetailListByContainerSubNo(outboundTaskBindDtList, containerDto.getContainerNo());
                // ?????????Map?????? Key=outTaskDetailId,Value=bindingNum
                Map<String, Float> map = relaOutboundTaskBindDetailList.stream().collect(Collectors.toMap(OutboundTaskBindDetail::getOutbTaskBindId, OutboundTaskBindDetail::getBindingNum));
                containerSubDto.setContainerAndOutDetailBindingMap(map);
                containerSubDtoList.add(containerSubDto);
            }
            containerDto.setContainerSubList(containerSubDtoList);
            resultList.add(containerDto);
        }
        return resultList;
    }

    @Override
    public List<OutboundTaskBindVo> findByPickingOrderId(String id) {
        List<OutboundTaskBind> outboundTaskBindList = outboundTaskBindMapper.findByMap(MapUtils.put("pickingOrderId", id).getMap(), OutboundTaskBind.class);
        List<OutboundTaskBindVo> outboundTaskBindVoList = new ArrayList<>();
        for (OutboundTaskBind outboundTaskBind : outboundTaskBindList){
            OutboundTaskBindVo outboundTaskBindVo = new OutboundTaskBindVo();
            outboundTaskBindVo.setContainerNo(outboundTaskBind.getContainerNo());
            outboundTaskBindVo.setCreateTime(outboundTaskBind.getCreateTime());
            outboundTaskBindVo.setPickingOrderId(outboundTaskBind.getPickingOrderId());
            outboundTaskBindVo.setStationId(outboundTaskBind.getStationId());
            outboundTaskBindVo.setId(outboundTaskBind.getId());
            outboundTaskBindVo.setFinishTime(outboundTaskBind.getFinishTime());
            outboundTaskBindVo.setOrderPoolId(outboundTaskBind.getOrderPoolId());
            List<OutboundTaskBindDetail> byMap = outboundTaskBindDtMapper.findByMap(MapUtils.put("outbTaskBindId", outboundTaskBind.getId()).getMap(), OutboundTaskBindDetail.class);
            outboundTaskBindVo.setOutboundTaskBindDetailList(byMap);
            outboundTaskBindVo.setDetailSize(byMap.size());
            outboundTaskBindVoList.add(outboundTaskBindVo);
        }
        return outboundTaskBindVoList;
    }

    /**
     * ??????????????????->???????????????????????????????????????
     * @param outboundTaskBindDetailList ???????????????????????????
     * @param containerNoSub ????????????
     */
    private List<OutboundTaskBindDetail> getOutboundTaskBindDetailListByContainerSubNo(
            List<OutboundTaskBindDetail> outboundTaskBindDetailList,
            String containerNoSub) {
        if (CollectionUtils.isEmpty(outboundTaskBindDetailList)) {
            return Lists.newArrayList();
        }
        if (StringUtils.isEmpty(containerNoSub)) {
            return Lists.newArrayList();
        }
        List<OutboundTaskBindDetail> relaOutboundTaskBindDetailList = Lists.newArrayList();
        for (OutboundTaskBindDetail outboundTaskBindDetail : outboundTaskBindDetailList) {
            if (containerNoSub.equals(outboundTaskBindDetail.getContainerNoSub())) {
                relaOutboundTaskBindDetailList.add(outboundTaskBindDetail);
            }
        }
        return relaOutboundTaskBindDetailList;
    }
}
