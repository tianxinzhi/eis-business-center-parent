package com.prolog.eis.bc.service.inbound.impl;

import com.google.common.collect.Lists;
import com.prolog.eis.bc.dao.inbound.InboundTaskHisMapper;
import com.prolog.eis.bc.dao.inbound.InboundTaskMapper;
import com.prolog.eis.bc.facade.dto.inbound.InboundTaskDto;
import com.prolog.eis.bc.facade.dto.inbound.WmsInboundTaskDto;
import com.prolog.eis.bc.facade.vo.inbound.InboundTaskDetailVo;
import com.prolog.eis.bc.facade.vo.inbound.InboundTaskVo;
import com.prolog.eis.bc.feign.EisInvContainerStoreSubFeign;
import com.prolog.eis.bc.feign.EisWarehouseStationFeign;
import com.prolog.eis.bc.feign.WmsInboundFeign;
import com.prolog.eis.bc.feign.container.EisContainerRouteClient;
import com.prolog.eis.bc.feign.container.EisControllerClient;
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
import com.prolog.framework.dao.util.PageUtils;
import com.prolog.framework.utils.MapUtils;
import com.prolog.upcloud.base.inventory.vo.EisInvContainerStoreVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private WmsInboundFeign wmsInboundFeign;

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
    public void cancelTask(InboundTask dto) throws Exception {
        InboundTask inboundTask = inboundTaskMapper.findByMap(
                MapUtils.put("upperSystemTaskId", dto.getUpperSystemTaskId()).getMap(), InboundTask.class)
                .stream().findFirst().orElse(null);
        if (null == inboundTask) {
            throw new NotFoundException("取消失败:任务不存在");
        }
        inboundTask.setStatus(InboundTask.TASK_STATUS_CANCEL);
        inboundTask.setFinishTime(new Date());
        //汇总转历史
        toHistory(inboundTask);
        //明细转历史
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
            throw new Exception("ID不能为空！");
        }
        InboundTaskHis his = new InboundTaskHis();
        BeanUtils.copyProperties(inboundTask, his);
        inboundTaskHisMapper.save(his);
        inboundTaskMapper.deleteById(inboundTask.getId(), InboundTask.class);
    }

    @Override
    public void applyContainer(ZxMcsInBoundResponseDto dto) throws Exception {
        PortInfo portInfo = validated(dto);
        //TODO 请求WMS拿数据
        Map<String, Object> map = MapUtils.put("containerNo", dto.getStockId()).getMap();
        RestMessage<WmsInboundTaskDto> wmsRest = wmsInboundFeign.inboundTask(JsonHelper.toJson(map));
        if (!wmsRest.isSuccess()) {
            throw new PrologException(String.format("容器{%}入库申请失败，{%s}", dto.getStockId(), wmsRest.getMessage()));
        }
        WmsInboundTaskDto wmsInboundTaskDto = wmsRest.getData();
        if (null == wmsInboundTaskDto) {
            throw new PrologException(String.format("容器{%}入库申请失败，WMS返回数据为空", dto.getStockId()));
        }
        //生成标准入库单
        InboundTaskVo inboundTaskVo = convertDto(dto, portInfo, wmsInboundTaskDto);
        createInboundTask(inboundTaskVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createInboundTask(InboundTaskVo vo) {
        if (null == vo || CollectionUtils.isEmpty(vo.getInboundTaskDetailVoList())) {
            throw new NullParameterException("数据集为空");
        }
        InboundTask inboundTask = new InboundTask();
        BeanUtils.copyProperties(vo, inboundTask);
        inboundTask.setStatus(InboundTask.TASK_STATUS_NOTSTART);
        inboundTask.setCreateTime(new Date());
        inboundTaskMapper.save(inboundTask);

        List<InboundTaskDetailVo> detailList = vo.getInboundTaskDetailVoList();
        for (InboundTaskDetailVo detailVo : detailList) {
            InboundTaskDetail inboundTaskDetail = new InboundTaskDetail();
            BeanUtils.copyProperties(detailVo, inboundTaskDetail);
            inboundTaskDetail.setInboundTaskId(inboundTask.getId());
            inboundTaskDetail.setTaskId(PrologStringUtils.newGUID());
            inboundTaskDetail.setDetailStatus(InboundTask.TASK_STATUS_NOTSTART);
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

    /**
     * 校验
     *
     * @param dto
     */
    private PortInfo validated(ZxMcsInBoundResponseDto dto) throws Exception {
        //调用库存服务，找容器是否存在
        RestMessage<List<EisInvContainerStoreVo>> containerRest = eisInvContainerStoreSubFeign.findByContainerNo(dto.getStockId());
        if (!containerRest.isSuccess()) {
            throw new PrologException(String.format("容器{%}入库申请失败，{%s}", dto.getStockId(), containerRest.getMessage()));
        }
        List<EisInvContainerStoreVo> containerList = containerRest.getData();
        if (!CollectionUtils.isEmpty(containerList)) {
            throw new PrologException(String.format("容器{%}入库申请失败，库存已存在", dto.getStockId()));
        }
        //调用路径服务，找容器是否存在
        Map<String, Object> map = MapUtils.put("containerNo", dto.getStockId()).getMap();
        RestMessage<String> locationRest = eisContainerRouteClient.findContainerLocation(JsonHelper.toJson(map));
        if (!locationRest.isSuccess()) {
            throw new PrologException(String.format("容器{%}入库申请失败，{%s}", dto.getStockId(), locationRest.getMessage()));
        }
        String data = locationRest.getData();
        ContainerLocationRespDto containerLocationRespDto = JsonHelper.getObject(data, ContainerLocationRespDto.class);
        if (ContainerLocationRespDto.CONTAINER_ERROR_STATE != containerLocationRespDto.getState()) {
            throw new PrologException(String.format("容器{%}入库申请失败，容器位置已存在", dto.getStockId()));
        }
        //调用仓库服务，找区域配置
        RestMessage<WhArea> areaRest = eisWarehouseStationFeign.getAreaByLocation(dto.getSource());
        if (!areaRest.isSuccess()) {
            throw new PrologException(String.format("容器{%}入库申请失败，{%s}", dto.getStockId(), areaRest.getMessage()));
        }
        WhArea whArea = areaRest.getData();
        if (null == whArea) {
            throw new PrologException(String.format("容器{%}入库申请失败，坐标区域不存在", dto.getStockId()));
        }
        //调用控制服务，找入库口配置
        RestMessage<PortInfo> portRest = eisControllerClient.getPortByArea(whArea.getAreaNo());
        if (!portRest.isSuccess()) {
            throw new PrologException(String.format("容器{%}入库申请失败，{%s}", dto.getStockId(), portRest.getMessage()));
        }
        PortInfo portInfo = portRest.getData();
        if (null == portInfo) {
            throw new PrologException(String.format("容器{%}入库申请失败，出入口资料不存在", dto.getStockId()));
        }
        return portInfo;
    }

    /**
     * 实体转换
     *
     * @param dto
     * @param portInfo
     * @param wmsInboundTaskDto
     */
    private InboundTaskVo convertDto(ZxMcsInBoundResponseDto dto, PortInfo portInfo, WmsInboundTaskDto wmsInboundTaskDto) {
        InboundTaskVo vo = new InboundTaskVo();
        vo.setUpperSystemTaskId(wmsInboundTaskDto.getUpperSystemTaskId());
        //明细
        InboundTaskDetailVo detailVo = new InboundTaskDetailVo();
        detailVo.setContainerNo(dto.getStockId());
        detailVo.setContainerType(wmsInboundTaskDto.getContainerType());
        detailVo.setPortNo(portInfo.getPortNo());
        detailVo.setSourceArea(portInfo.getAreaNo());
        detailVo.setSourceLocation(dto.getSource());
        detailVo.setWeight(Double.valueOf(dto.getWeight()));
        detailVo.setHeight(Double.valueOf(dto.getHeight()));
        //子任务
        InboundTaskDetailSub sub = new InboundTaskDetailSub();
        sub.setContainerNoSub(StringUtils.isEmpty(dto.getStockIdSub()) ? dto.getStockId() : dto.getStockIdSub());
        sub.setItemId(wmsInboundTaskDto.getItemId());
        sub.setLotId(wmsInboundTaskDto.getLotId());
        sub.setQty(wmsInboundTaskDto.getQty());
        List<InboundTaskDetailSub> subList = Lists.newArrayList();
        subList.add(sub);
        detailVo.setInboundTaskDetailSubList(subList);

        List<InboundTaskDetailVo> detailList = Lists.newArrayList();
        detailList.add(detailVo);
        vo.setInboundTaskDetailVoList(detailList);
        return vo;
    }
}
