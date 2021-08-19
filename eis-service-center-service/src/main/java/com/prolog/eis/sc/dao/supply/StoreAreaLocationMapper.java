package com.prolog.eis.sc.dao.supply;

import com.prolog.eis.model.route.supply.StoreAreaLocation;
import com.prolog.eis.sc.dto.supply.StoreAreaLocationDto;
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
public interface StoreAreaLocationMapper extends BaseMapper<StoreAreaLocation> {

    /**
     * 可执行的配置
     *
     * @return
     */
    @Select({"select t.* from store_area_location t where t.is_available = 1 order by t.create_time"})
    @Results(id = "storeSupplyConfigDtoMap", value = {
            @Result(property = "storeArea", column = "store_area"), @Result(property = "isAvailable", column = "is_available"),
            @Result(property = "createTime", column = "create_time")
    })
    List<StoreAreaLocationDto> findListDto();
}
