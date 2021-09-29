package com.prolog.eis.bc.dao.businesscenter;

import com.prolog.eis.core.model.biz.container.ContainerTask;
import com.prolog.eis.bc.facade.dto.businesscenter.BusiContainerTaskDto;
import com.prolog.framework.dao.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

public interface BusiContainerTaskMapper extends BaseMapper<ContainerTask>  {
    @Select("<script>" +
            "SELECT biz_eis_container_task.*, DT.count FROM biz_eis_container_task " +
            "LEFT JOIN (SELECT  container_task_id,COUNT(container_task_id) AS count FROM biz_eis_container_task_dt GROUP BY container_task_id) DT " +
            "ON DT.container_task_id = biz_eis_container_task.id " +
            "WHERE 1= 1 " +
            "<if test='dto.upperSystemTaskId !=null    and dto.upperSystemTaskId != \"\"'>"+
            " and upper_system_task_id LIKE CONCAT('%',#{dto.upperSystemTaskId},'%')" +
            "</if >" +
            "<if test='dto.typeNo !=null  and dto.typeNo != \"\"'>"+
            " and type_no LIKE CONCAT('%',#{dto.typeNo},'%')" +
            "</if >" +
            "<if test='dto.status !=null  and dto.status != \"\"'>"+
            " and status =#{dto.status} " +
            "</if >" +
            "<if test='dto.createTimeFrom !=null  '>" +
            " and date(create_time) >=#{dto.createTimeFrom } " +
            "</if >" +
            "<if test='dto.createTimeTo !=null  '>" +
            " and date(create_time) &lt;=#{dto.createTimeTo } " +
            "</if >" +
            "</script>")
    @Results({@Result(column = "id", property = "id", jdbcType = JdbcType.VARCHAR),
            @Result(column = "upper_system_task_id", property = "upperSystemTaskId", jdbcType = JdbcType.VARCHAR),
            @Result(column = "type_no", property = "typeNo", jdbcType = JdbcType.VARCHAR),
            @Result(column = "status", property = "status", jdbcType = JdbcType.VARCHAR),
            @Result(column = "priority", property = "priority", jdbcType = JdbcType.VARCHAR),
            @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.DATE),
            @Result(column = "task_start_time", property = "taskStartTime", jdbcType = JdbcType.DATE),
            @Result(column = "task_finish_time", property = "taskFinishTime", jdbcType = JdbcType.DATE),
            @Result(column = "count", property = "detailCount", jdbcType = JdbcType.VARCHAR)})
    public List<BusiContainerTaskDto> getBusiContainerTaskPage(@Param("dto") BusiContainerTaskDto dto);
}
