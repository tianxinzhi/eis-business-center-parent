package com.prolog.eis.bc.service.pickingorder.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.prolog.eis.bc.facade.vo.OutboundTaskBindVo;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.prolog.eis.bc.dao.PickingOrderMapper;
import com.prolog.eis.bc.facade.dto.businesscenter.PickingOrderDto2;
import com.prolog.eis.bc.service.outboundtask.OutboundTaskBindService;
import com.prolog.eis.bc.service.outboundtask.OutboundTaskService;
import com.prolog.eis.bc.service.pickingorder.PickingOrderService;
import com.prolog.eis.component.algorithm.composeorder.entity.BizOutTask;
import com.prolog.eis.component.algorithm.composeorder.entity.ContainerDto;
import com.prolog.eis.component.algorithm.composeorder.entity.PickingOrderDto;
import com.prolog.eis.core.model.biz.outbound.PickingOrder;
import com.prolog.framework.core.restriction.Criteria;
import com.prolog.framework.core.restriction.Restriction;
import com.prolog.framework.core.restriction.Restrictions;
import com.prolog.framework.dao.util.PageUtils;
import com.prolog.framework.core.pojo.Page;


@Service
public class PickingOrderServiceImpl implements PickingOrderService {

    @Autowired
    private PickingOrderMapper pickingOrderMapper;

    @Autowired
    private OutboundTaskBindService outboundTaskBindService;

    @Autowired
    @Qualifier("outboundTaskServiceImpl")
    private OutboundTaskService outboundTaskService;


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
    public String insert(String stationId,List<String> outTaskIdList) throws Exception {
        if (StringUtils.isEmpty(stationId)) {
            throw new Exception("????????????????????????stationId is null");
        }
        if (CollectionUtils.isEmpty(outTaskIdList)) {
            throw new Exception("????????????Id??????????????????");
        }
        PickingOrder insertObj = new PickingOrder();
        insertObj.setStationId(stationId);
        // ?????????0=?????????????????????
        insertObj.setIsAllArrive(0);
        insertObj.setStartTime(new Date());
        insertObj.setCreateTime(new Date());
        long effectNum = pickingOrderMapper.save(insertObj);
        if (effectNum != 1) {
            throw new Exception("????????????????????????????????????:" + effectNum);
        }
        outboundTaskService.batchUpdatePickingOrderId(outTaskIdList,insertObj.getId());
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

        // ????????????????????????OutboundTask
        List<String> pickingOrderIdList = pickingOrderList.stream().map(PickingOrder::getId).collect(Collectors.toList());
        List<BizOutTask> allBizOutTaskList = outboundTaskService.findByPickingOrderIdList(pickingOrderIdList);
        // ??????????????????????????????
        List<ContainerDto> allContainerList = outboundTaskBindService.findByPickingOrderIdList(pickingOrderIdList, storeMatchingStrategy);

        if (CollectionUtils.isEmpty(pickingOrderList)) {
            return Lists.newArrayList();
        }
        List<PickingOrderDto> pickingOrderDtoList = Lists.newArrayList();
        // ???????????????????????????
        for (PickingOrder pickingOrder : pickingOrderList) {
            PickingOrderDto pickingOrderDto = new PickingOrderDto();
            pickingOrderDto.setId(pickingOrder.getId());
            pickingOrderDto.setStationId(pickingOrder.getStationId());
            pickingOrderDto.setIsAllContainerArrive(pickingOrder.getIsAllArrive());

            // ?????????pickingOrderId?????????outTask
            List<BizOutTask> outboundTaskList = Lists.newArrayList();
            for (BizOutTask bizOutTask : allBizOutTaskList) {
                if (null != pickingOrder.getId() && pickingOrder.getId().equals(bizOutTask.getPickOrderId())) {
                    outboundTaskList.add(bizOutTask);
                }
            }
            pickingOrderDto.setOutboundTaskList(outboundTaskList);
            
            // ???????????????????????????????????????????????????
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
    
    public Page<PickingOrder> getPickingOrderPage(PickingOrderDto2 dto){
        PageUtils.startPage(dto.getPageNum(), dto.getPageSize());
        Criteria criteria = new Criteria(PickingOrder.class);
        Restriction r1 = null;
        Restriction r2 = null;

        if (dto.getCreateTimeFrom() != null) {
            r1 = Restrictions.ge("createTime", dto.getCreateTimeFrom());
        }
        if (dto.getCreateTimeTo() != null) {
            r2 = Restrictions.le("createTime", dto.getCreateTimeTo());
        }

        criteria.setRestriction(Restrictions.and(r1,r2));

        List<PickingOrder> list = pickingOrderMapper.findByCriteria(criteria);
        return PageUtils.getPage(list);
    }

    @Override
    public List<OutboundTaskBindVo> getContainerByPickingOrderId(String id) {
        return outboundTaskBindService.findByPickingOrderId(id);
    }

}
