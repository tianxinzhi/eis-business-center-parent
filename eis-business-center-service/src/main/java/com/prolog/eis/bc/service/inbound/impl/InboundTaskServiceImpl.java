package com.prolog.eis.bc.service.inbound.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.prolog.eis.bc.dao.inbound.InboundTaskHisMapper;
import com.prolog.eis.bc.dao.inbound.InboundTaskMapper;
import com.prolog.eis.bc.facade.dto.inbound.InboundTaskDto;
import com.prolog.eis.bc.facade.dto.inbound.InboundTaskHisDto;
import com.prolog.eis.bc.facade.dto.inbound.MasterInboundTaskDto;
import com.prolog.eis.bc.facade.dto.inbound.MasterInboundTaskSubDto;
import com.prolog.eis.bc.facade.vo.inbound.InboundTaskDetailHisVo;
import com.prolog.eis.bc.facade.vo.inbound.InboundTaskDetailVo;
import com.prolog.eis.bc.facade.vo.inbound.InboundTaskHisVo;
import com.prolog.eis.bc.facade.vo.inbound.InboundTaskVo;
import com.prolog.eis.bc.feign.EisInvContainerStoreSubFeign;
import com.prolog.eis.bc.feign.EisWarehouseStationFeign;
import com.prolog.eis.bc.feign.MasterInboundFeign;
import com.prolog.eis.bc.feign.container.EisContainerRouteClient;
import com.prolog.eis.bc.feign.container.EisControllerClient;
import com.prolog.eis.bc.service.FeignService;
import com.prolog.eis.bc.service.inbound.InboundTaskDetailService;
import com.prolog.eis.bc.service.inbound.InboundTaskDetailSubService;
import com.prolog.eis.bc.service.inbound.InboundTaskService;
import com.prolog.eis.common.util.JsonHelper;
import com.prolog.eis.common.util.ListHelper;
import com.prolog.eis.common.util.PrologStringUtils;
import com.prolog.eis.core.dto.route.ContainerLocationRespDto;
import com.prolog.eis.core.model.base.area.WhArea;
import com.prolog.eis.core.model.biz.inbound.InboundTask;
import com.prolog.eis.core.model.biz.inbound.InboundTaskDetail;
import com.prolog.eis.core.model.biz.inbound.InboundTaskDetailSub;
import com.prolog.eis.core.model.biz.inbound.InboundTaskHis;
import com.prolog.eis.core.model.ctrl.area.PortInfo;
import com.prolog.eis.inter.dto.mcs.ZxMcsInBoundResponseDto;
import com.prolog.framework.common.message.RestMessage;
import com.prolog.framework.core.exception.NotFoundException;
import com.prolog.framework.core.exception.NullParameterException;
import com.prolog.framework.core.exception.PrologException;
import com.prolog.framework.core.pojo.Page;
import com.prolog.framework.core.restriction.Criteria;
import com.prolog.framework.core.restriction.Restrictions;
import com.prolog.framework.dao.util.PageUtils;
import com.prolog.framework.utils.MapUtils;
import com.prolog.upcloud.base.inventory.vo.EisInvContainerStoreVo;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: wuxl
 * @create: 2021-10-18 17:52
 * @Version: V1.0
 */
@Service
@Slf4j
public class InboundTaskServiceImpl implements InboundTaskService {
    @Autowired
    private InboundTaskMapper inboundTaskMapper;
    @Autowired
    private InboundTaskHisMapper inboundTaskHisMapper;
    @Autowired
    private InboundTaskDetailService inboundTaskDetailService;
    @Autowired
    private InboundTaskDetailSubService inboundTaskDetailSubService;
    @Autowired
    private EisInvContainerStoreSubFeign eisInvContainerStoreSubFeign;
    @Autowired
    private EisContainerRouteClient eisContainerRouteClient;
    @Autowired
    private EisWarehouseStationFeign eisWarehouseStationFeign;
    @Autowired
    private EisControllerClient eisControllerClient;
    @Autowired
    private MasterInboundFeign masterInboundFeign;
    @Autowired
    private FeignService feignService;

    @Override
    public Page<InboundTaskVo> listInboundTaskByPage(InboundTaskDto dto) {
        PageUtils.startPage(dto.getPageNum(), dto.getPageSize());
        List<InboundTaskVo> inboundTaskVoList = inboundTaskMapper.findByParam(dto);
        List<InboundTaskDetailVo> inboundTaskDetailVoList = inboundTaskDetailService.listInboundTaskDetailByParam(null);
        inboundTaskVoList.forEach(vo -> {
            List<InboundTaskDetailVo> where = ListHelper.where(inboundTaskDetailVoList, d -> vo.getId().equals(d.getInboundTaskId()));
            vo.setDetailSize(where.size());
            vo.setInboundTaskDetailVoList(where);
        });
        return PageUtils.getPage(inboundTaskVoList);
    }

    @Override
    public Page<InboundTaskHisVo> listInboundTaskHisByPage(InboundTaskHisDto dto) {
        PageUtils.startPage(dto.getPageNum(), dto.getPageSize());
        List<InboundTaskHisVo> inboundTaskHisVoList = inboundTaskHisMapper.findByParam(dto);
        List<InboundTaskDetailHisVo> inboundTaskDetailHisVoList = inboundTaskDetailService.listInboundTaskDetailHisByParam(null);
        inboundTaskHisVoList.forEach(vo -> {
            List<InboundTaskDetailHisVo> where = ListHelper.where(inboundTaskDetailHisVoList, d -> vo.getId().equals(d.getInboundTaskId()));
            vo.setDetailSize(where.size());
            vo.setInboundTaskDetailHisVoList(where);
        });
        return PageUtils.getPage(inboundTaskHisVoList);
    }

    @Override
    public List<InboundTaskVo> listInboundTask(InboundTaskDto dto) {
        List<InboundTaskVo> inboundTaskVoList = inboundTaskMapper.findByParam(dto);
        List<InboundTaskDetailVo> inboundTaskDetailVoList = inboundTaskDetailService.listInboundTaskDetailByParam(null);
        inboundTaskVoList.forEach(vo -> {
            List<InboundTaskDetailVo> where = ListHelper.where(inboundTaskDetailVoList, d -> vo.getId().equals(d.getInboundTaskId()));
            vo.setDetailSize(where.size());
            vo.setInboundTaskDetailVoList(where);
        });
        return inboundTaskVoList;
    }

    @Override
    public void cancelTask(InboundTask dto) throws Exception {
        InboundTask inboundTask = inboundTaskMapper.findByMap(
                MapUtils.put("upperSystemTaskId", dto.getUpperSystemTaskId()).getMap(), InboundTask.class)
                .stream().findFirst().orElse(null);
        if (null == inboundTask) {
            throw new NotFoundException("????????????:???????????????");
        }
        inboundTask.setStatus(InboundTask.TASK_STATUS_CANCEL);
        inboundTask.setFinishTime(new Date());
        //???????????????
        toHistory(inboundTask);
        //???????????????
        List<InboundTaskDetailVo> inboundTaskDetailVoList = inboundTaskDetailService.listInboundTaskDetailByParam(inboundTask.getId());
        for (InboundTaskDetailVo detail : inboundTaskDetailVoList) {
            inboundTask.setStatus(InboundTask.TASK_STATUS_CANCEL);
            inboundTask.setFinishTime(new Date());
            inboundTaskDetailService.toHistory(detail);
            List<InboundTaskDetailSub> inboundTaskDetailSubList = inboundTaskDetailSubService.listInboundTaskDetailSubByParam(detail.getId());
            for (InboundTaskDetailSub sub : inboundTaskDetailSubList) {
                inboundTaskDetailSubService.toHistory(sub.getId());
            }
        }
    }

    @Override
    public void toHistory(InboundTask inboundTask) throws Exception {
        if (StringUtils.isEmpty(inboundTask.getId())) {
            throw new Exception("ID???????????????");
        }
        InboundTaskHis his = new InboundTaskHis();
        BeanUtils.copyProperties(inboundTask, his);
        inboundTaskHisMapper.save(his);
        inboundTaskMapper.deleteById(inboundTask.getId(), InboundTask.class);
    }

    @Override
    public void applyContainer(ZxMcsInBoundResponseDto dto) throws Exception {
        PortInfo portInfo = validated(dto);
        Map<String, Object> map = MapUtils.put("containerNo", dto.getStockId()).getMap();
        RestMessage<MasterInboundTaskDto> masterRest;
        //TODO ??????WMS?????????
        if (StringUtils.isEmpty(dto.getTarget())) {
            masterRest = masterInboundFeign.inboundTask(JsonHelper.toJson(map));
        } else {
            masterRest = testData(dto);
        }
        if (!masterRest.isSuccess()) {
            log.error(String.format("[inboundTask]??????????????????????????????%s", masterRest.getMessage()));
            throw new PrologException(String.format("??????{%s}?????????????????????{%s}", dto.getStockId(), masterRest.getMessage()));
        }
        MasterInboundTaskDto masterInboundTaskDto = masterRest.getData();
        if (null == masterInboundTaskDto) {
            throw new PrologException(String.format("??????{%s}?????????????????????WMS??????????????????", dto.getStockId()));
        }
        //?????????????????????
        InboundTaskVo inboundTaskVo = convertDto(dto, portInfo, masterInboundTaskDto);
        createInboundTask(inboundTaskVo);
    }

    private RestMessage<MasterInboundTaskDto> testData(ZxMcsInBoundResponseDto dto) {
        RestMessage<MasterInboundTaskDto> restMessage = new RestMessage<>();

        MasterInboundTaskDto dto1 = new MasterInboundTaskDto();
        dto1.setUpperSystemTaskId(PrologStringUtils.newGUID());
        dto1.setContainerNo(dto.getStockId());
        dto1.setContainerType(1);
        dto1.setBusinessProperty("111@222");

        MasterInboundTaskSubDto dto2 = new MasterInboundTaskSubDto();
        dto2.setContainerSubNo(dto.getStockId());
        dto2.setItemId("111");
        dto2.setLotId("222");
        dto2.setQty(99);

        List<MasterInboundTaskSubDto> subList = Lists.newArrayList();
        subList.add(dto2);
        dto1.setSubList(subList);

        restMessage.setSuccess(true);
        restMessage.setMessage("????????????");
        restMessage.setCode("200");
        restMessage.setData(dto1);
        return restMessage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createInboundTask(InboundTaskVo vo) {
        if (null == vo || CollectionUtils.isEmpty(vo.getInboundTaskDetailVoList())) {
            throw new NullParameterException("???????????????");
        }
        InboundTask inboundTask = new InboundTask();
        BeanUtils.copyProperties(vo, inboundTask);
        inboundTask.setCreateTime(new Date());
        inboundTaskMapper.save(inboundTask);

        List<InboundTaskDetailVo> detailList = vo.getInboundTaskDetailVoList();
        for (InboundTaskDetailVo detailVo : detailList) {
            InboundTaskDetail inboundTaskDetail = new InboundTaskDetail();
            BeanUtils.copyProperties(detailVo, inboundTaskDetail);
            inboundTaskDetail.setInboundTaskId(inboundTask.getId());
            inboundTaskDetail.setTaskId(PrologStringUtils.newGUID());
            inboundTaskDetail.setCreateTime(new Date());
            inboundTaskDetailService.save(inboundTaskDetail);

            List<InboundTaskDetailSub> subList = detailVo.getInboundTaskDetailSubList();
            if (!CollectionUtils.isEmpty(subList)) {
                for (InboundTaskDetailSub sub : subList) {
                    sub.setInboundTaskDetailId(inboundTaskDetail.getId());
                    sub.setCreateTime(new Date());
                }
                inboundTaskDetailSubService.saveBatch(subList);
            }
        }
    }

    @Override
    public long updateById(String id, Map<String, Object> map) {
        return inboundTaskMapper.updateMapById(id, map, InboundTask.class);
    }

    /**
     * ??????
     *
     * @param dto
     */
    private PortInfo validated(ZxMcsInBoundResponseDto dto) throws Exception {
        //??????????????????????????????????????????
        RestMessage<List<EisInvContainerStoreVo>> containerRest = eisInvContainerStoreSubFeign.findByContainerNo(dto.getStockId());
        if (!containerRest.isSuccess()) {
            log.error(String.format("[findByContainerNo]????????????????????????%s", containerRest.getMessage()));
            throw new PrologException(String.format("??????{%s}?????????????????????{%s}", dto.getStockId(), containerRest.getMessage()));
        }
        List<EisInvContainerStoreVo> containerList = containerRest.getData();
        if (!CollectionUtils.isEmpty(containerList)) {
            throw new PrologException(String.format("??????{%s}????????????????????????????????????", dto.getStockId()));
        }
        //??????????????????????????????????????????
        Map<String, Object> map = MapUtils.put("containerNo", dto.getStockId()).getMap();
        RestMessage<String> locationRest = eisContainerRouteClient.findContainerLocation(JsonHelper.toJson(map));
        if (!locationRest.isSuccess()) {
            log.error(String.format("[findContainerLocation]??????????????????????????????%s", locationRest.getMessage()));
            throw new PrologException(String.format("??????{%s}?????????????????????{%s}", dto.getStockId(), locationRest.getMessage()));
        }
        String data = locationRest.getData();
        ContainerLocationRespDto containerLocationRespDto = JsonHelper.getObject(data, ContainerLocationRespDto.class);
        if (ContainerLocationRespDto.CONTAINER_ERROR_STATE != containerLocationRespDto.getState()) {
            throw new PrologException(String.format("??????{%s}??????????????????????????????????????????", dto.getStockId()));
        }
        //????????????????????????????????????
        RestMessage<WhArea> areaRest = eisWarehouseStationFeign.getAreaByLocation(dto.getSource());
        if (!areaRest.isSuccess()) {
            log.error(String.format("[getAreaByLocation]????????????????????????%s", areaRest.getMessage()));
            throw new PrologException(String.format("??????{%s}?????????????????????{%s}", dto.getStockId(), areaRest.getMessage()));
        }
        WhArea whArea = areaRest.getData();
        if (null == whArea) {
            throw new PrologException(String.format("??????{%s}??????????????????????????????????????????", dto.getStockId()));
        }
        //???????????????????????????????????????
        RestMessage<PortInfo> portRest = eisControllerClient.getPortByArea(whArea.getAreaNo());
        if (!portRest.isSuccess()) {
            log.error(String.format("[getPortByArea]???????????????????????????%s", portRest.getMessage()));
            throw new PrologException(String.format("??????{%s}?????????????????????{%s}", dto.getStockId(), portRest.getMessage()));
        }
        PortInfo portInfo = portRest.getData();
        if (null == portInfo) {
            throw new PrologException(String.format("??????{%s}?????????????????????????????????????????????", dto.getStockId()));
        }
        return portInfo;
    }

    /**
     * ????????????
     *
     * @param dto
     * @param portInfo
     * @param wmsInboundTaskDto
     */
    private InboundTaskVo convertDto(ZxMcsInBoundResponseDto dto, PortInfo portInfo, MasterInboundTaskDto wmsInboundTaskDto) {
        InboundTaskVo vo = new InboundTaskVo();
        vo.setUpperSystemTaskId(wmsInboundTaskDto.getUpperSystemTaskId());
        vo.setStatus(InboundTask.TASK_STATUS_NOTSTART);
        //??????
        InboundTaskDetailVo detailVo = new InboundTaskDetailVo();
        detailVo.setContainerNo(dto.getStockId());
        detailVo.setContainerType(wmsInboundTaskDto.getContainerType());
        detailVo.setPortNo(portInfo.getPortNo());
        detailVo.setSourceArea(portInfo.getAreaNo());
        detailVo.setSourceLocation(dto.getSource());
        detailVo.setWeight(Double.valueOf(dto.getWeight()));
        detailVo.setDetailStatus(InboundTask.TASK_STATUS_NOTSTART);
        detailVo.setHeight(Double.valueOf(dto.getHeight()));
        detailVo.setBusinessProperty(wmsInboundTaskDto.getBusinessProperty());
        //?????????
        List<InboundTaskDetailSub> subList = Lists.newArrayList();
        for (MasterInboundTaskSubDto subDto : wmsInboundTaskDto.getSubList()) {
            InboundTaskDetailSub sub = new InboundTaskDetailSub();
            sub.setContainerNoSub(StringUtils.isEmpty(subDto.getContainerSubNo())? dto.getStockId() : subDto.getContainerSubNo());
            sub.setItemId(subDto.getItemId());
            sub.setLotId(subDto.getLotId());
            sub.setQty(subDto.getQty());
            subList.add(sub);
        }
        detailVo.setInboundTaskDetailSubList(subList);

        List<InboundTaskDetailVo> detailList = Lists.newArrayList();
        detailList.add(detailVo);
        vo.setInboundTaskDetailVoList(detailList);
        return vo;
    }

    @Override
    public List<InboundTask> getListByIdList(List<String> taskIdList) {
        if (CollectionUtils.isEmpty(taskIdList)) {
            return Lists.newArrayList();
        }
        Criteria inboundTaskCrt = Criteria.forClass(InboundTask.class);
        inboundTaskCrt.setRestriction(Restrictions.in("id", taskIdList.toArray()));
        return inboundTaskMapper.findByCriteria(inboundTaskCrt);
    }

}
