package com.prolog.eis.bc.service.pickingorder.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.prolog.eis.bc.dao.PickingOrderMapper;
import com.prolog.eis.bc.service.outboundtask.OutboundTaskBindService;
import com.prolog.eis.bc.service.outboundtask.OutboundTaskService;
import com.prolog.eis.bc.service.pickingorder.PickingOrderService;
import com.prolog.eis.component.algorithm.composeorder.entity.BizOutTask;
import com.prolog.eis.component.algorithm.composeorder.entity.ContainerDto;
import com.prolog.eis.component.algorithm.composeorder.entity.PickingOrderDto;
import com.prolog.eis.core.model.biz.outbound.PickingOrder;
import com.prolog.framework.core.restriction.Criteria;
import com.prolog.framework.core.restriction.Restrictions;

@Service
public class PickingOrderServiceImpl implements PickingOrderService {

    @Autowired
    private PickingOrderMapper pickingOrderMapper;

    @Autowired
    private OutboundTaskBindService outboundTaskBindService;

    @Autowired
    private OutboundTaskService outboundTaskService;

    @Override
    public String insert(String stationId) throws Exception {
        if (StringUtils.isEmpty(stationId)) {
            throw new Exception("生成拣选单失败，stationId is null");
        }
        PickingOrder insertObj = new PickingOrder();
        insertObj.setStationId(stationId);
        // 默认为0=容器未全部到达
        insertObj.setIsAllArrive(0);
        insertObj.setStartTime(new Date());
        long effectNum = pickingOrderMapper.save(insertObj);
        if (effectNum != 1) {
            throw new Exception("生成拣选单失败，影响行数:" + effectNum);
        }
        return insertObj.getId();
    }

    @Override
    public List<PickingOrderDto> findByStationId(String stationId, int storeMatchingStrategy) {
        if (StringUtils.isEmpty(stationId)) {
            return Lists.newArrayList();
        }
        Criteria crt = Criteria.forClass(PickingOrder.class);
        crt.setRestriction(Restrictions.eq("stationId", stationId));
        List<PickingOrder> pickingOrderList = pickingOrderMapper.findByCriteria(crt);
        if (CollectionUtils.isEmpty(pickingOrderList)) {
            return Lists.newArrayList();
        }

        // 查询拣选单关联的OutboundTask
        List<String> pickingOrderIdList = pickingOrderList.stream().map(PickingOrder::getId).collect(Collectors.toList());
        List<BizOutTask> allBizOutTaskList = outboundTaskService.findByPickingOrderIdList(pickingOrderIdList);
        // 查询拣选单关联的容器
        List<ContainerDto> allContainerList = outboundTaskBindService.findByPickingOrderIdList(pickingOrderIdList, storeMatchingStrategy);

        if (CollectionUtils.isEmpty(pickingOrderList)) {
            return Lists.newArrayList();
        }
        List<PickingOrderDto> pickingOrderDtoList = Lists.newArrayList();
        // 转化为业务数据对象
        for (PickingOrder pickingOrder : pickingOrderList) {
            PickingOrderDto pickingOrderDto = new PickingOrderDto();
            pickingOrderDto.setId(pickingOrder.getId());
            pickingOrderDto.setStationId(pickingOrder.getStationId());
            pickingOrderDto.setIsAllContainerArrive(pickingOrder.getIsAllArrive());

            // 筛选出pickingOrderId关联的outTask
            List<BizOutTask> outboundTaskList = Lists.newArrayList();
            for (BizOutTask bizOutTask : allBizOutTaskList) {
                if (null != pickingOrder.getId() && pickingOrder.getId().equals(bizOutTask.getPickOrderId())) {
                    outboundTaskList.add(bizOutTask);
                }
            }
            pickingOrderDto.setOutboundTaskList(outboundTaskList);
            
            // 计算容器对应总商品数量，已绑定数量
            List<ContainerDto> containerList = Lists.newArrayList();
            for (ContainerDto container : allContainerList) {
                if (null != pickingOrder.getId() && pickingOrder.getId().equals(container.getPickingOrderId())) {
                    containerList.add(container);
                }
            }
            pickingOrderDto.setContainerList(containerList);
            pickingOrderDtoList.add(pickingOrderDto);
        }
        return pickingOrderDtoList;
    }

}
