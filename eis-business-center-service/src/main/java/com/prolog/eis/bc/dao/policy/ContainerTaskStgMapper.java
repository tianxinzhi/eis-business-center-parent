package com.prolog.eis.bc.dao.policy;

import java.util.List;

import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Repository;

import com.prolog.eis.bc.facade.dto.policy.ContainerTaskStrategyDto;
import com.prolog.eis.core.model.ctrl.container.ContainerTaskStrategy;
import com.prolog.framework.dao.mapper.BaseMapper;

/**
 * @Author: hzw
 * @Date: 2021/9/27
 * @Desc:
 */
@Repository
public interface ContainerTaskStgMapper extends BaseMapper<ContainerTaskStrategy> {

    @Select("<script>" +
            "select id,container_task_type_no,type_name,create_time" +
            " from ctrl_eis_container_task_stg" +
            " where 1=1" +
            " <if test='containerTaskTypeNo != null and containerTaskTypeNo != \"\"'>" +
            " and container_task_type_no like concat('%',#{containerTaskTypeNo},'%')" +
            " </if>" +
            " <if test='typeName != null and typeName != \"\"'>" +
            " and type_name like concat('%',#{typeName},'%')" +
            " </if>" +
            " <if test='startTime != null and startTime != \"\" and endTime != null and endTime != \"\"'>" +
            " and (create_time &gt;= #{startTime} and create_time &lt;= #{endTime} )" +
            " </if>" +
            " <if test='startTime != null and startTime != \"\" and (endTime == null || endTime == \"\") '>" +
            " and create_time &gt;= #{startTime}" +
            " </if>" +
            " <if test='(startTime == null || startTime == \"\") and endTime != null and endTime != \"\"'>" +
            " and create_time &lt;= #{endTime}" +
            " </if>" +
            "</script>")
    @Results({@Result(column = "container_task_type_no", property = "containerTaskTypeNo", jdbcType = JdbcType.VARCHAR),
            @Result(column = "type_name", property = "typeName", jdbcType = JdbcType.VARCHAR),
            @Result(column = "id", property = "id", jdbcType = JdbcType.VARCHAR),
            @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.DATETIMEOFFSET)})
    List<ContainerTaskStrategy> getContainerTaskStg(ContainerTaskStrategyDto dto);
    
}
