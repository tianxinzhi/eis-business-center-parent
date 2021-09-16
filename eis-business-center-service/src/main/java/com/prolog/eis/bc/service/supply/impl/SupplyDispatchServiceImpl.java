package com.prolog.eis.bc.service.supply.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.prolog.eis.bc.feign.container.CarryInterfaceFeign;
import com.prolog.eis.bc.service.supply.SupplyDispatchService;
import com.prolog.eis.common.util.PrologStringUtils;
import com.prolog.eis.common.util.location.LocationConstants;
import com.prolog.eis.component.core.dto.business.order.OutboundTaskDetailDto;
import com.prolog.eis.component.core.dto.business.supply.ContainerStoreDto;
import com.prolog.eis.component.core.dto.business.supply.SafeSupplyDto;
import com.prolog.eis.component.core.dto.business.supply.StoreSupplyDetailDto;
import com.prolog.eis.component.core.dto.business.supply.StoreSupplyDto;
import com.prolog.eis.component.core.dto.business.supply.UrgentSupplyDto;
import com.prolog.eis.core.model.biz.carry.CarryTask;
import com.prolog.eis.fx.component.business.service.supply.StoreSupplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * @author: wuxl
 * @create: 2021-09-14 15:22
 * @Version: V1.0
 */
@Service
@Slf4j
public class SupplyDispatchServiceImpl implements SupplyDispatchService {
    @Autowired
    private StoreSupplyService storeSupplyService;
    @Autowired
    private CarryInterfaceFeign carryInterfaceFeign;

    @Override
    public void safeSupplyDispatch() throws Exception {
        //数据初始化
        SafeSupplyDto safeSupplyDto = storeSupplyService.safeInit();
        // TODO 需调整为调用库存服务/仓库服务/路径服务，需计算 到位+正在过来的-正在离开的（搬运任务）
        safeSupplyDto.setContainerStoreList(Lists.newArrayList());
        List<StoreSupplyDetailDto> supplyDetailList = storeSupplyService.findSafeSupplyDetailList(safeSupplyDto);
        if (CollectionUtils.isEmpty(supplyDetailList)) {
            return;
        }
        StoreSupplyDto supply = storeSupplyService.findSafeSupply(safeSupplyDto.getStoreSupplyDtoList(), supplyDetailList);
        if (null == supply) {
            return;
        }
        //找容器
        ContainerStoreDto container = storeSupplyService.findContainer(safeSupplyDto.getContainerStoreList(), supply.getSupplyDetailDto().getItemId(), supply.getSupplyDetailDto().getLotId());
        //生成搬运任务
        createCarryTask(container.getContainerNo(), container.getContainerNo(), container.getAreaNo(), container.getLocationNo(), supply.getTargetArea());
    }

    @Override
    public void urgentSupplyDispatch() throws Exception {
        //数据初始化
        UrgentSupplyDto urgentSupplyDto = storeSupplyService.urgentInit();
        // TODO 需调整为调用库存服务/仓库服务/路径服务，需计算 到位+正在过来的-正在离开的（搬运任务）
        urgentSupplyDto.setContainerStoreList(Lists.newArrayList());
        List<OutboundTaskDetailDto> urgentSupplyDetailList = storeSupplyService.findUrgentSupplyDetailList(urgentSupplyDto);
        if (CollectionUtils.isEmpty(urgentSupplyDetailList)) {
            return;
        }
        StoreSupplyDto supply = storeSupplyService.findUrgentSupply(urgentSupplyDto.getStoreSupplyDtoList(), urgentSupplyDetailList);
        if (null == supply) {
            return;
        }
        //找容器
        ContainerStoreDto container = storeSupplyService.findContainer(urgentSupplyDto.getContainerStoreList(), supply.getSupplyDetailDto().getItemId(), supply.getSupplyDetailDto().getLotId());
        //生成搬运任务
        createCarryTask(container.getContainerNo(), container.getContainerNo(), container.getAreaNo(), container.getLocationNo(), supply.getTargetArea());
    }

    /**
     * 生成搬运任务
     *
     * @param palletNo      任务号
     * @param containerNo   容器号
     * @param startRegion   起点区域
     * @param startLocation 起点坐标
     * @param endRegion     终点区域
     * @throws Exception
     */
    private long createCarryTask(String palletNo, String containerNo, String startRegion, String startLocation, String endRegion) {
        CarryTask carryTask = new CarryTask();
        carryTask.setId(PrologStringUtils.newGUID());
        carryTask.setPalletNo(palletNo);
        carryTask.setContainerNo(containerNo);
        carryTask.setTaskType(LocationConstants.PATH_TASK_TYPE_CARRY);
        carryTask.setStartRegion(startRegion);
        carryTask.setStartLocation(startLocation);
        carryTask.setEndRegion(endRegion);
        carryTask.setPriority(99);
        carryTask.setCreateTime(new Date());
        try {
            String json = JSONObject.toJSONString(carryTask);
            carryInterfaceFeign.createCarry(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }
}
