package com.prolog.eis.sc.service.supply.impl;

import com.prolog.eis.model.route.supply.StoreAreaLocationDetail;
import com.prolog.eis.sc.dao.container.ScContainerStoreMapper;
import com.prolog.eis.sc.dao.container.ScContainerStoreSubMapper;
import com.prolog.eis.sc.dao.supply.StoreAreaLocationDetailMapper;
import com.prolog.eis.sc.dao.supply.StoreAreaLocationMapper;
import com.prolog.eis.sc.dao.supply.StoreSupplyConfigMapper;
import com.prolog.eis.sc.dao.supply.StoreSupplyGoodsConfigMapper;
import com.prolog.eis.sc.dto.supply.ContainerStoreDto;
import com.prolog.eis.sc.dto.supply.StoreAreaLocationDto;
import com.prolog.eis.sc.dto.supply.StoreSupplyConfigDto;
import com.prolog.eis.sc.dto.supply.StoreSupplyGoodsConfigDto;
import com.prolog.eis.sc.dto.supply.SupplyDto;
import com.prolog.eis.sc.service.supply.StoreSupplyService;
import com.prolog.eis.util.ListHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

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
            for (StoreSupplyGoodsConfigDto goodsDto : supplyConfigDto.getGoodsConfigList()) {
                int goodsId = goodsDto.getGoodsId();
                //目标区域商品库存数
                int targetCount = (int) ListHelper.where(supplyDto.getContainerStoreMap().get(goodsId), c -> supplyConfigDto.getTargetArea().equals(c.getAreaNo())).stream().count();
                if (targetCount >= goodsDto.getQty()) {
                    continue;
                }
                goodsDto.setTargetQty(targetCount);
                //源区域商品库存
                List<ContainerStoreDto> where = ListHelper.where(supplyDto.getContainerStoreMap().get(goodsId), c -> supplyConfigDto.getSourceArea().equals(c.getAreaNo()));
                if (CollectionUtils.isEmpty(where)) {
                    return;
                }
                goodsDto.setSourceQty(where.size());
            }

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
        List<StoreSupplyGoodsConfigDto> storeSupplyGoodsConfigList = storeSupplyGoodsConfigMapper.findListDto();
        Assert.isTrue(!CollectionUtils.isEmpty(storeSupplyConfigList) && !CollectionUtils.isEmpty(storeSupplyGoodsConfigList), "补货策略配置有误");
        storeSupplyConfigList.forEach(storeSupplyConfig -> {
            List<StoreSupplyGoodsConfigDto> where = ListHelper.where(storeSupplyGoodsConfigList,
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
        // TODO 需调整为调用WMS服务，需计算 到位+正在过来的-正在离开的（搬运任务）
        return dto;
    }

    /**
     * 找托盘
     *
     * @param where
     */
    private void findContainer(List<ContainerStoreDto> where) {

    }

}