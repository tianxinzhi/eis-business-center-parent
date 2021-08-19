package com.prolog.eis.sc.service.supply.impl;

import com.prolog.eis.model.route.supply.StoreAreaLocationDetail;
import com.prolog.eis.model.route.supply.StoreSupplyGoodsConfig;
import com.prolog.eis.sc.dao.container.ScContainerStoreMapper;
import com.prolog.eis.sc.dao.container.ScContainerStoreSubMapper;
import com.prolog.eis.sc.dao.supply.StoreAreaLocationDetailMapper;
import com.prolog.eis.sc.dao.supply.StoreAreaLocationMapper;
import com.prolog.eis.sc.dao.supply.StoreSupplyConfigMapper;
import com.prolog.eis.sc.dao.supply.StoreSupplyGoodsConfigMapper;
import com.prolog.eis.sc.dto.supply.ContainerStoreDto;
import com.prolog.eis.sc.dto.supply.ContainerStoreSubDto;
import com.prolog.eis.sc.dto.supply.StoreAreaLocationDto;
import com.prolog.eis.sc.dto.supply.StoreSupplyConfigDto;
import com.prolog.eis.sc.dto.supply.SupplyDto;
import com.prolog.eis.sc.service.supply.StoreSupplyService;
import com.prolog.eis.util.ListHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: wuxl
 * @create: 2021-08-18 14:58
 * @Version: V1.0
 */
@Service
@Slf4j
public class StoreSupplyServiceImpl implements StoreSupplyService {
    @Autowired
    private StoreSupplyConfigMapper storeSupplyConfigMapper;
    @Autowired
    private StoreSupplyGoodsConfigMapper storeSupplyGoodsConfigMapper;
    @Autowired
    private StoreAreaLocationMapper storeAreaLocationMapper;
    @Autowired
    private StoreAreaLocationDetailMapper storeAreaLocationDetailMapper;
    @Autowired
    private ScContainerStoreMapper containerStoreMapper;
    @Autowired
    private ScContainerStoreSubMapper containerStoreSubMapper;


    @Override
    public void safeSupplyDispatch() throws Exception {
        //数据初始化
        SupplyDto supplyDto = init();
        supplyDto.getStoreSupplyConfigDtoList().forEach(supplyConfigDto -> {
            String sourceArea = supplyConfigDto.getSourceArea();
            Map<Integer, Integer> map = supplyConfigDto.getGoodsConfigList().stream().collect(Collectors.toMap(StoreSupplyGoodsConfig::getGoodsId, StoreSupplyGoodsConfig::getQty));

            map.forEach((goods, qty) -> {
                List<ContainerStoreSubDto> storeSubDtoList = ListHelper.where(supplyDto.getContainerStoreSubList(), c -> goods == c.getGoodsId());
                for (ContainerStoreSubDto subDto : storeSubDtoList) {
                    List<ContainerStoreDto> storeDtoList = ListHelper.where(subDto.getStoreDtoList(), c -> sourceArea.equals(c.getAreaNo()));
                    if (CollectionUtils.isEmpty(storeDtoList)) {
                        continue;
                    }
                }
            });

            String targetArea = supplyConfigDto.getTargetArea();
        });

    }

    @Override
    public void urgentSupplyDispatch() throws Exception {

    }

    /**
     * 数据初始化
     *
     * @return
     */
    private SupplyDto init() {
        SupplyDto dto = new SupplyDto();
        //补货策略配置
        List<StoreSupplyConfigDto> storeSupplyConfigList = storeSupplyConfigMapper.findListDto();
        List<StoreSupplyGoodsConfig> storeSupplyGoodsConfigList = storeSupplyGoodsConfigMapper.findByMap(null, StoreSupplyGoodsConfig.class);
        Assert.isTrue(!CollectionUtils.isEmpty(storeSupplyConfigList) && !CollectionUtils.isEmpty(storeSupplyGoodsConfigList), "补货策略配置有误");
        storeSupplyConfigList.forEach(storeSupplyConfig -> {
            List<StoreSupplyGoodsConfig> where = ListHelper.where(storeSupplyGoodsConfigList,
                    goods -> storeSupplyConfig.getId() == goods.getStoreSupplyConfigId());
            storeSupplyConfig.setGoodsConfigList(where);
        });
        dto.setStoreSupplyConfigDtoList(storeSupplyConfigList);

        //库存区域关系
        List<StoreAreaLocationDto> storeAreaLocationList = storeAreaLocationMapper.findListDto();
        List<StoreAreaLocationDetail> storeAreaLocationDetailList = storeAreaLocationDetailMapper.findByMap(null, StoreAreaLocationDetail.class);
        Assert.isTrue(!CollectionUtils.isEmpty(storeAreaLocationList) && !CollectionUtils.isEmpty(storeAreaLocationDetailList), "库存区域关系配置有误");
        storeAreaLocationList.forEach(storeAreaLocation -> {
            List<StoreAreaLocationDetail> where = ListHelper.where(storeAreaLocationDetailList,
                    detail -> storeAreaLocation.getId() == detail.getStoreAreaLocationId());
            storeAreaLocation.setLocationDetailList(where);
        });
        dto.setStoreAreaLocationDtoList(storeAreaLocationList);

        //托盘位置
        List<ContainerStoreDto> containerStoreList = containerStoreMapper.findListDto();
        List<ContainerStoreSubDto> containerStoreSubList = containerStoreSubMapper.findListDto();
        containerStoreSubList.forEach(storeSubDto -> {
            List<ContainerStoreDto> where = ListHelper.where(containerStoreList, c -> storeSubDto.getContainerStoreId() == c.getId());
            storeSubDto.setStoreDtoList(where);
        });
        //dto.setContainerStoreDtoList(containerStoreList);
        dto.setContainerStoreSubList(containerStoreSubList);
        return dto;
    }
}
