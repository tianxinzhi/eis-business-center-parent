package com.prolog.eis.bc.service.inbound;

import com.google.common.collect.Lists;
import com.prolog.eis.bc.facade.dto.inbound.InboundTaskDto;
import com.prolog.eis.bc.facade.vo.inbound.InboundTaskDetailVo;
import com.prolog.eis.bc.facade.vo.inbound.InboundTaskVo;
import com.prolog.eis.bc.feign.container.EisContainerRouteClient;
import com.prolog.eis.bc.feign.container.EisContainerStoreFeign;
import com.prolog.eis.bc.feign.container.EisControllerClient;
import com.prolog.eis.common.util.JsonHelper;
import com.prolog.eis.common.util.ListHelper;
import com.prolog.eis.common.util.constants.CommonConstants;
import com.prolog.eis.common.util.constants.DispatchSwitchConstants;
import com.prolog.eis.common.util.constants.SysParamConstants;
import com.prolog.eis.common.util.location.LocationConstants;
import com.prolog.eis.core.dto.inboundallot.InboundAllotAreaParamDto;
import com.prolog.eis.core.dto.inboundallot.InboundAllotAreaResultDto;
import com.prolog.eis.core.dto.route.CarryTaskCallbackDto;
import com.prolog.eis.core.dto.route.ItemContainerReqDto;
import com.prolog.eis.core.model.biz.carry.CarryTask;
import com.prolog.eis.core.model.biz.inbound.InboundTask;
import com.prolog.eis.core.model.biz.inbound.InboundTaskDetail;
import com.prolog.eis.core.model.biz.inbound.InboundTaskDetailSub;
import com.prolog.framework.bz.common.search.SearchApi;
import com.prolog.framework.common.message.RestMessage;
import com.prolog.framework.core.exception.PrologException;
import com.prolog.framework.core.exception.UpdateException;
import com.prolog.framework.utils.MapUtils;
import com.prolog.upcloud.base.inventory.vo.EisInvContainerStoreSubVo;
import com.prolog.upcloud.base.inventory.vo.EisInvContainerStoreVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * @author: wuxl
 * @create: 2021-10-20 13:53
 * @Version: V1.0
 */
@Service
@Slf4j
public class InboundDispatch {
    @Autowired
    private SearchApi searchApi;
    @Autowired
    private InboundTaskService inboundTaskService;
    @Autowired
    private InboundTaskDetailService inboundTaskDetailService;
    @Autowired
    private InboundTaskDetailSubService inboundTaskDetailSubService;
    @Autowired
    private InboundTaskReportService inboundTaskReportService;
    @Autowired
    private EisContainerRouteClient eisContainerRouteClient;
    @Autowired
    private EisControllerClient eisControllerClient;
    @Autowired
    private EisContainerStoreFeign eisContainerStoreFeign;

    /**
     * ????????????
     *
     * @throws Exception
     */
    public void inboundSchedule() throws Exception {
        //????????????
        RestMessage<Boolean> restMessage = eisControllerClient.getSwitchByCode(DispatchSwitchConstants.DISPATCH_SWITCH_INBOUND_TASK);
        if (!restMessage.isSuccess()) {
            log.error(String.format("??????????????????????????????????????????%s", restMessage.getMessage()));
            throw new PrologException(String.format("??????????????????????????????????????????%s", restMessage.getMessage()));
        }
        if (!restMessage.getData()) {
            return;
        }
        try {
            //???????????????
            List<InboundTaskVo> list = init();
            if (CollectionUtils.isEmpty(list)) {
                return;
            }
            //????????????
            List<InboundTaskVo> cancelList = ListHelper.where(list, vo -> InboundTask.TASK_STATUS_CANCEL == vo.getStatus());
            if (!CollectionUtils.isEmpty(cancelList)) {
                for (InboundTaskVo inboundTaskVo : cancelList) {
                    cancelTask(inboundTaskVo);
                }
                list.removeAll(cancelList);
            }
            //??????????????????
            List<InboundTaskVo> executeList = ListHelper.where(list, vo -> InboundTask.TASK_STATUS_NOTSTART == vo.getStatus() || InboundTask.TASK_STATUS_GOINGON == vo.getStatus());
            if (!CollectionUtils.isEmpty(executeList)) {
                for (InboundTaskVo inboundTaskVo : executeList) {
                    executeTask(inboundTaskVo);
                }
            }
            //??????????????????????????????
            List<InboundTaskVo> taskList = ListHelper.where(executeList, vo -> InboundTask.TASK_STATUS_GOINGON == vo.getStatus());
            if (!CollectionUtils.isEmpty(taskList)) {
                for (InboundTaskVo inboundTaskVo : taskList) {
                    callbackTask(inboundTaskVo);
                }
            }
            //????????????
            //List<InboundTaskVo> finishList = ListHelper.where(list, vo -> InboundTask.TASK_STATUS_FINISH == vo.getStatus());
            //if (!CollectionUtils.isEmpty(finishList)) {
            //    for (InboundTaskVo inboundTaskVo : finishList) {
            //        finishTask(inboundTaskVo);
            //    }
            //}
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * ???????????????
     *
     * @return
     */
    private List<InboundTaskVo> init() {
        return inboundTaskService.listInboundTask(new InboundTaskDto());
    }

    /**
     * ????????????
     *
     * @param inboundTaskVo
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelTask(InboundTaskVo inboundTaskVo) throws Exception {
        InboundTask inboundTask = new InboundTask();
        BeanUtils.copyProperties(inboundTaskVo, inboundTask);
        inboundTask.setFinishTime(new Date());
        inboundTaskService.toHistory(inboundTask);

        List<InboundTaskDetailVo> inboundTaskDetailVoList = inboundTaskVo.getInboundTaskDetailVoList();
        for (InboundTaskDetailVo detailVo : inboundTaskDetailVoList) {
            InboundTaskDetail inboundTaskDetail = new InboundTaskDetail();
            BeanUtils.copyProperties(detailVo, inboundTaskDetail);
            inboundTaskDetail.setFinishTime(new Date());
            inboundTaskDetailService.toHistory(inboundTaskDetail);

            List<InboundTaskDetailSub> inboundTaskDetailSubList = detailVo.getInboundTaskDetailSubList();
            for (InboundTaskDetailSub sub : inboundTaskDetailSubList) {
                inboundTaskDetailSubService.toHistory(sub.getId());
            }
        }
        inboundTaskReportService.toReport(inboundTask, inboundTaskVo.getLocation());
    }

    /**
     * ???????????????????????????
     *
     * @param inboundTaskVo
     */
    @Transactional(rollbackFor = Exception.class)
    public void executeTask(InboundTaskVo inboundTaskVo) throws Exception {
        List<InboundTaskDetailVo> detailList = inboundTaskVo.getInboundTaskDetailVoList();
        if (CollectionUtils.isEmpty(detailList)) {
            return;
        }
        for (InboundTaskDetailVo detailVo : detailList) {
            if (InboundTask.TASK_STATUS_NOTSTART != detailVo.getDetailStatus()) {
                continue;
            }
            //????????????????????????
            InboundAllotAreaResultDto areaData = findArea(detailVo);
            if (null == areaData) {
                log.error(String.format("??????{%s}???????????????????????????", detailVo.getContainerNo()));
                continue;
            }
            //??????????????????
            createContainerLocation(detailVo);
            //??????????????????
            createCarryTask(detailVo, areaData);
            //????????????
            createStore(detailVo);
            //??????????????????
            updateForStart(detailVo, inboundTaskVo);
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param inboundTaskVo
     */
    @Transactional(rollbackFor = Exception.class)
    public void callbackTask(InboundTaskVo inboundTaskVo) throws Exception {
        List<InboundTaskDetailVo> detailList = inboundTaskVo.getInboundTaskDetailVoList();
        if (CollectionUtils.isEmpty(detailList)) {
            return;
        }
        RestMessage<List<CarryTaskCallbackDto>> carryRest = eisContainerRouteClient.findAllCallback();
        if (!carryRest.isSuccess()) {
            log.error(String.format("[findAllCallback]????????????????????????????????????%s", carryRest.getMessage()));
            throw new PrologException(String.format("?????????????????????????????????{%s}", carryRest.getMessage()));
        }
        List<CarryTaskCallbackDto> carryList = carryRest.getData();
        if (CollectionUtils.isEmpty(carryList)) {
            return;
        }
        List<CarryTaskCallbackDto> newCarryList = Lists.newArrayList();
        for (InboundTaskDetailVo detailVo : detailList) {
            if (InboundTask.TASK_STATUS_GOINGON != detailVo.getDetailStatus()) {
                continue;
            }
            CarryTaskCallbackDto callback = ListHelper.where(carryList, c -> detailVo.getTaskId().equals(c.getId())).stream().findAny().orElse(null);
            if (null == callback) {
                continue;
            }
            detailVo.setDetailStatus(InboundTask.TASK_STATUS_FINISH);
            newCarryList.add(callback);
            inboundTaskVo.setLocation(callback.getEndLocation());
        }
        RestMessage<String> restMessage = eisContainerRouteClient.toCallbackHisList(newCarryList);
        if (!restMessage.isSuccess()) {
            log.error(String.format("[toCallbackHisList]???????????????????????????????????????%s", restMessage.getMessage()));
            throw new PrologException(String.format("????????????????????????????????????{%s}", restMessage.getMessage()));
        }
        //??????????????????
        updateForFinish(inboundTaskVo);
    }

    /**
     * ????????????
     *
     * @param inboundTaskVo
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void finishTask(InboundTaskVo inboundTaskVo) throws Exception {
        cancelTask(inboundTaskVo);
    }

    /**
     * ???????????????
     *
     * @param detailVo
     * @return
     */
    private InboundAllotAreaResultDto findArea(InboundTaskDetailVo detailVo) {
        InboundAllotAreaParamDto dto = new InboundAllotAreaParamDto();
        dto.setContainerNo(detailVo.getContainerNo());
        dto.setBusinessProperty(detailVo.getBusinessProperty());
        dto.setPortNo(detailVo.getPortNo());
        dto.setWeight(detailVo.getWeight());
        RestMessage<InboundAllotAreaResultDto> allotAreaRest = eisContainerRouteClient.getAllotArea(dto);
        if (!allotAreaRest.isSuccess()) {
            log.error(String.format("[getAllotArea]??????????????????????????????%s", allotAreaRest.getMessage()));
            throw new PrologException(String.format("??????{%s}???????????????????????????{%s}", dto.getContainerNo(), allotAreaRest.getMessage()));
        }
        return allotAreaRest.getData();
    }

    /**
     * ??????????????????
     *
     * @param detailVo
     * @throws Exception
     */
    private void createContainerLocation(InboundTaskDetailVo detailVo) {
        ItemContainerReqDto dto = new ItemContainerReqDto();
        dto.setLocationNo(detailVo.getSourceLocation());
        dto.setContainerNo(detailVo.getContainerNo());
        dto.setContainerType(String.valueOf(detailVo.getContainerType()));
        dto.setBusinessProperty(detailVo.getBusinessProperty());
        try {
            RestMessage<String> containerRest = eisContainerRouteClient.createItemContainer(dto);
            if (!containerRest.isSuccess()) {
                log.error(String.format("[createCarry]??????????????????????????????%s", containerRest.getMessage()));
                throw new PrologException(String.format("??????{%s}???????????????????????????{%s}", detailVo.getContainerNo(), containerRest.getMessage()));
            }
        } catch (Exception e) {
            throw new PrologException(e.toString());
        }
    }

    /**
     * ??????????????????
     *
     * @param detailVo
     * @param areaData
     * @throws Exception
     */
    private void createCarryTask(InboundTaskDetailVo detailVo, InboundAllotAreaResultDto areaData) throws Exception {
        CarryTask carryTask = new CarryTask();
        carryTask.setId(detailVo.getTaskId());
        carryTask.setContainerNo(detailVo.getContainerNo());
        carryTask.setPalletNo(detailVo.getContainerNo());
        carryTask.setTaskType(LocationConstants.PATH_TASK_TYPE_CARRY);
        carryTask.setStartRegion(detailVo.getSourceArea());
        carryTask.setStartLocation(detailVo.getSourceLocation());
        carryTask.setEndRegion(areaData.getAreaNo());
        carryTask.setTaskStatus(CommonConstants.CARRY_TASK_STATUS_NOT_STARTED);
        carryTask.setPriority(80);
        carryTask.setBusinessProperty(detailVo.getBusinessProperty());
        carryTask.setHeight(detailVo.getHeight());
        carryTask.setWeight(detailVo.getWeight());
        carryTask.setCreateTime(new Date());
        String data = JsonHelper.toJson(carryTask);
        try {
            RestMessage<String> carryRest = eisContainerRouteClient.createCarry(data);
            if (!carryRest.isSuccess()) {
                log.error(String.format("[createCarry]??????????????????????????????%s", carryRest.getMessage()));
                throw new PrologException(String.format("??????{%s}???????????????????????????{%s}", detailVo.getContainerNo(), carryRest.getMessage()));
            }
        } catch (Exception e) {
            throw new PrologException(e.toString());
        }
    }

    /**
     * ????????????
     *
     * @param detailVo
     */
    private void createStore(InboundTaskDetailVo detailVo) {
        RestMessage<String> sysRest = eisControllerClient.getValueByCode(SysParamConstants.INVENTORY_MANAGEMENT);
        if (!sysRest.isSuccess()) {
            log.error(String.format("[getValueByCode]??????????????????????????????%s", sysRest.getMessage()));
            throw new PrologException("????????????????????????!" + sysRest.getMessage());
        }
        int isStore = Integer.parseInt(sysRest.getData());
        if (isStore == CommonConstants.NO) {
            return;
        }
        EisInvContainerStoreVo vo = new EisInvContainerStoreVo();
        vo.setContainerNo(detailVo.getContainerNo());
        vo.setContainerType(detailVo.getContainerType());

        List<InboundTaskDetailSub> inboundTaskDetailSubList = detailVo.getInboundTaskDetailSubList();
        List<EisInvContainerStoreSubVo> subList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(inboundTaskDetailSubList)) {
            for (InboundTaskDetailSub sub : inboundTaskDetailSubList) {
                EisInvContainerStoreSubVo subVo = new EisInvContainerStoreSubVo();
                subVo.setContainerStoreSubNo(sub.getContainerNoSub());
                subVo.setItemId(sub.getItemId());
                subVo.setLotId(sub.getLotId());
                subVo.setQty(sub.getQty());
                subList.add(subVo);
            }
        }
        vo.setContainerStoreSubList(subList);

        try {
            RestMessage<String> storeRest = eisContainerStoreFeign.saveContainerStore(vo);
            if (!storeRest.isSuccess()) {
                log.error(String.format("[saveContainerStore]????????????????????????%s", storeRest.getMessage()));
                throw new PrologException("??????????????????!" + storeRest.getMessage());
            }
        } catch (Exception e) {
            throw new PrologException(e.toString());
        }
    }

    /**
     * ??????????????????
     *
     * @param detailVo
     */
    private void updateForStart(InboundTaskDetailVo detailVo, InboundTaskVo inboundTaskVo) {
        long l1 = inboundTaskService.updateById(detailVo.getInboundTaskId()
                , MapUtils.put("status", InboundTask.TASK_STATUS_GOINGON).put("startTime", new Date()).getMap());
        long l2 = inboundTaskDetailService.updateById(detailVo.getId()
                , MapUtils.put("detailStatus", InboundTask.TASK_STATUS_GOINGON).put("startTime", new Date()).getMap());
        if (l1 == 0L || l2 == 0L) {
            throw new UpdateException("????????????????????????");
        }
        detailVo.setDetailStatus(InboundTask.TASK_STATUS_GOINGON);
        inboundTaskVo.setStatus(InboundTask.TASK_STATUS_GOINGON);
    }

    /**
     * ??????????????????
     *
     * @param inboundTaskVo
     */
    private void updateForFinish(InboundTaskVo inboundTaskVo) throws Exception {
        List<InboundTaskDetailVo> inboundTaskDetailVoList = inboundTaskVo.getInboundTaskDetailVoList();
        List<InboundTaskDetailVo> where = ListHelper.where(inboundTaskDetailVoList, t -> InboundTask.TASK_STATUS_FINISH == t.getDetailStatus() && null == t.getFinishTime());
        for (InboundTaskDetailVo detailVo : where) {
            inboundTaskDetailService.updateById(detailVo.getId()
                    , MapUtils.put("detailStatus", InboundTask.TASK_STATUS_FINISH).put("finishTime", new Date()).getMap());
            detailVo.setDetailStatus(InboundTask.TASK_STATUS_FINISH);
        }
        //?????????????????????
        if (inboundTaskDetailVoList.size() == where.size()) {
            //inboundTaskService.updateById(inboundTaskVo.getId()
            //        , MapUtils.put("status", InboundTask.TASK_STATUS_FINISH).put("finishTime", new Date()).getMap());
            inboundTaskVo.setStatus(InboundTask.TASK_STATUS_FINISH);
            cancelTask(inboundTaskVo);
        }
    }
}
