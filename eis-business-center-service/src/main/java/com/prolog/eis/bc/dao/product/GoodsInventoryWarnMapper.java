package com.prolog.eis.bc.dao.product;

import com.prolog.eis.bc.facade.dto.product.GoodsInventoryInfoDto;
import com.prolog.eis.bc.facade.dto.product.GoodsInventoryWarnDefineDto;
import com.prolog.eis.core.model.biz.route.ContainerLocation;
import com.prolog.framework.dao.mapper.BaseMapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: txz
 * @Date: 2021/9/14 11:43
 */
@Repository
public interface GoodsInventoryWarnMapper extends BaseMapper<ContainerLocation> {

    /**
     * 获取商品库存信息
     * @param dto
     * @return
     */
    @Results({@Result(column = "item_id", property = "itemId", jdbcType = JdbcType.VARCHAR),
            @Result(column = "lot_id", property = "lotId", jdbcType = JdbcType.VARCHAR),
            @Result(column = "source_location", property = "sourceLocation", jdbcType = JdbcType.VARCHAR),
            @Result(column = "orderQty", property = "orderQty", jdbcType = JdbcType.INTEGER)})
    @Select("<script>" +
            "select t1.item_id,t1.lot_id,t2.source_location,\n" +
            "sum(t1.binding_num) over(PARTITION by t1.item_id,t1.lot_id) orderQty\n" +
            "from biz_eis_out_task_bind_dt t1\n" +
            "left join biz_eis_route_container_loc t2\n" +
            "on t1.container_no = t2.container_no"+
            "</script>")
    List<GoodsInventoryInfoDto> page(GoodsInventoryWarnDefineDto dto);
}
