package com.prolog.eis.sc.dao.container;

import com.prolog.eis.model.route.stock.ContainerStoreSub;
import com.prolog.eis.sc.dto.supply.ContainerStoreSubDto;
import com.prolog.framework.dao.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: wuxl
 * @create: 2021-08-19 15:50
 * @Version: V1.0
 */
@Repository
@Deprecated
public interface ScContainerStoreSubMapper extends BaseMapper<ContainerStoreSub> {
    /**
     * 找所有子托盘
     *
     * @return
     */
    @Select({"select c.id, \r\n" +
            "c.container_store_id containerStoreId, \r\n" +
            "c.container_store_sub_no containerStoreSubNo, \r\n" +
            "c.owner_id ownerId, \r\n" +
            "c.goods_id goodsId, \r\n" +
            "c.lot_id lotId, \r\n" +
            "c.qty \r\n" +
            "from container_store_sub c"})
    List<ContainerStoreSubDto> findListDto();
}
