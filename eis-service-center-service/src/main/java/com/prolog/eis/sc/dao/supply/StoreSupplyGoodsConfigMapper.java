package com.prolog.eis.sc.dao.supply;

import com.prolog.eis.model.route.supply.StoreSupplyGoodsConfig;
import com.prolog.eis.sc.dto.supply.StoreSupplyGoodsConfigDto;
import com.prolog.framework.dao.mapper.BaseMapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: wuxl
 * @create: 2021-08-18 18:06
 * @Version: V1.0
 */
@Repository
public interface StoreSupplyGoodsConfigMapper extends BaseMapper<StoreSupplyGoodsConfig> {

    @Select({"select t.* from store_supply_goods_config t order by t.create_time"})
    @Results(id = "storeSupplyConfigDtoMap", value = {
            @Result(property = "storeSupplyConfigId", column = "store_supply_config_id"), @Result(property = "goodsId", column = "goods_id"),
            @Result(property = "createTime", column = "create_time")
    })
    List<StoreSupplyGoodsConfigDto> findListDto();
}
