package com.prolog.eis.bc.dao;

import com.prolog.eis.core.model.biz.outbound.OutboundTask;
import com.prolog.eis.bc.facade.dto.businesscenter.OutboundTaskDto;
import com.prolog.framework.dao.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

public interface OutTaskManagerDefineMapper extends BaseMapper<OutboundTask> {

    @Select("<script>" +
            "SELECT biz_eis_out_task.*, DT.count FROM biz_eis_out_task " +
            "LEFT JOIN (SELECT  out_task_id,COUNT(out_task_id) AS count FROM biz_eis_out_task_dt GROUP BY out_task_id) DT " +
            "ON DT.out_task_id = biz_eis_out_task.id " +
            "WHERE 1= 1 " +
            "<if test='dto.outTaskSmyId !=null     and dto.outTaskSmyId != \"\"'>"+
            " and out_task_smy_id LIKE CONCAT('%',#{dto.outTaskSmyId},'%')" +
            "</if >" +
            "<if test='dto.pickingOrderId !=null     and dto.pickingOrderId != \"\"'>"+
            " and picking_order_id LIKE CONCAT('%',#{dto.pickingOrderId},'%')" +
            "</if >" +
            "<if test='dto.upperSystemTaskId !=null     and dto.upperSystemTaskId != \"\"'>"+
            " and upper_system_task_id LIKE CONCAT('%',#{dto.upperSystemTaskId},'%')" +
            "</if >" +
            "<if test='dto.outboundTaskTypeNo !=null    and dto.outboundTaskTypeNo != \"\"'>"+
            " and outbound_task_type_no =#{dto.outboundTaskTypeNo} " +
            "</if >" +
            "<if test='dto.orderPoolId !=null    and dto.orderPoolId != \"\"'>"+
            " and order_pool_id LIKE CONCAT('%',#{dto.orderPoolId},'%')" +
            "</if >" +
            "<if test='dto.state !=null  '>" +
            " and state =#{dto.state} " +
            "</if >" +
            "<if test='dto.createTimeFrom !=null  '>" +
            " and date(create_time) >=#{dto.createTimeFrom } " +
            "</if >" +
            "<if test='dto.createTimeTo !=null  '>" +
            " and date(create_time) &lt;=#{dto.createTimeTo } " +
            "</if >" +
            "</script>")
    @Results({@Result(column = "id", property = "id", jdbcType = JdbcType.VARCHAR),
            @Result(column = "out_task_smy_id", property = "outTaskSmyId", jdbcType = JdbcType.VARCHAR),
            @Result(column = "upper_system_task_id", property = "upperSystemTaskId", jdbcType = JdbcType.VARCHAR),
            @Result(column = "outbound_task_type_no", property = "outboundTaskTypeNo", jdbcType = JdbcType.VARCHAR),
            @Result(column = "picking_order_id", property = "pickingOrderId", jdbcType = JdbcType.VARCHAR),
            @Result(column = "order_pool_id", property = "orderPoolId", jdbcType = JdbcType.VARCHAR),
            @Result(column = "state", property = "state", jdbcType = JdbcType.VARCHAR),
            @Result(column = "is_short_picking", property = "isShortPicking", jdbcType = JdbcType.VARCHAR),
            @Result(column = "priority", property = "priority", jdbcType = JdbcType.VARCHAR),
            @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.DATE),
            @Result(column = "start_time", property = "startTime", jdbcType = JdbcType.DATE),
            @Result(column = "finish_time", property = "finishTime", jdbcType = JdbcType.DATE),
            @Result(column = "count", property = "detailCount", jdbcType = JdbcType.VARCHAR)})
    public List<OutboundTaskDto> outboundTaskDetailDtoPage(@Param("dto") OutboundTaskDto dto);
}
