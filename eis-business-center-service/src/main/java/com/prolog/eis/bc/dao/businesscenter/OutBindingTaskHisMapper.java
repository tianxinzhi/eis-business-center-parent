package com.prolog.eis.bc.dao.businesscenter;

import com.prolog.eis.core.model.biz.outbound.OutboundTaskBindHis;
import com.prolog.eis.bc.facade.dto.businesscenter.OutBindingTaskHisDto;
import com.prolog.framework.dao.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

public interface OutBindingTaskHisMapper   extends BaseMapper<OutboundTaskBindHis> {

    @Select("<script>" +
            "SELECT biz_eis_out_task_bind_his.*, DT.count FROM biz_eis_out_task_bind_his " +
            "LEFT JOIN (SELECT  outb_task_bind_id,COUNT(outb_task_bind_id) AS count FROM biz_eis_out_task_bind_dt_his GROUP BY outb_task_bind_id) DT " +
            "ON DT.outb_task_bind_id = biz_eis_out_task_bind_his.id " +
            "WHERE 1= 1 " +
            "<if test='dto.containerNo !=null    and dto.containerNo != \"\"'>"+
            " and container_no LIKE CONCAT('%',#{dto.containerNo},'%')" +
            "</if >" +
            "<if test='dto.pickingOrderId !=null  and dto.pickingOrderId != \"\"'>"+
            " and picking_order_id LIKE CONCAT('%',#{dto.pickingOrderId},'%')" +
            "</if >" +
            "<if test='dto.orderPoolId !=null  and dto.orderPoolId != \"\"'>"+
            " and order_pool_id =#{dto.orderPoolId} " +
            "</if >" +
            "<if test='dto.createTimeFrom !=null  '>" +
            " and date(create_time) >=#{dto.createTimeFrom } " +
            "</if >" +
            "<if test='dto.createTimeTo !=null  '>" +
            " and date(create_time) &lt;=#{dto.createTimeTo } " +
            "</if >" +
            "</script>")
    @Results({@Result(column = "id", property = "id", jdbcType = JdbcType.VARCHAR),
            @Result(column = "container_no", property = "containerNo", jdbcType = JdbcType.VARCHAR),
            @Result(column = "picking_order_id", property = "pickingOrderId", jdbcType = JdbcType.VARCHAR),
            @Result(column = "station_id", property = "stationId", jdbcType = JdbcType.VARCHAR),
            @Result(column = "order_pool_id", property = "orderPoolId", jdbcType = JdbcType.VARCHAR),
            @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.DATE),
            @Result(column = "finish_time", property = "finishTime", jdbcType = JdbcType.DATE),
            @Result(column = "count", property = "detailCount", jdbcType = JdbcType.VARCHAR)})
    public List<OutBindingTaskHisDto> outboundTaskHisPage(@Param("dto") OutBindingTaskHisDto dto);
}