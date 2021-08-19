package com.prolog.eis.sc.dao.supply;

import com.prolog.eis.model.route.supply.StoreSupplyConfig;
import com.prolog.eis.sc.dto.supply.StoreSupplyConfigDto;
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
public interface StoreSupplyConfigMapper extends BaseMapper<StoreSupplyConfig> {

    /**
     * 可执行的配置
     *
     * @return
     */
    @Select({"select t.* from store_supply_config t where t.is_available = 1 order by t.create_time"})
    @Results(id = "storeSupplyConfigDtoMap", value = {
            @Result(property = "sourceArea", column = "source_area"), @Result(property = "targetArea", column = "target_area"),
            @Result(property = "isAvailable", column = "is_available"), @Result(property = "createTime", column = "create_time")
    })
    List<StoreSupplyConfigDto> findListDto();
}
