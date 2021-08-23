package com.prolog.eis.sc.dto.supply;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author: wuxl
 * @create: 2021-08-06 15:39
 * @Version: V1.0
 */
@Data
public class SupplyDto {

    /**
     * 库存区域货位关系集合
     */
    private List<StoreAreaLocationDto> storeAreaLocationDtoList;

    /**
     * 安全库存补货策略集合
     */
    private List<StoreSupplyConfigDto> storeSupplyConfigDtoList;

    /**
     * 容器库存集合(key=商品id, value=库存集合)
     */

    private Map<Integer, List<ContainerStoreDto>> ContainerStoreMap;
}
