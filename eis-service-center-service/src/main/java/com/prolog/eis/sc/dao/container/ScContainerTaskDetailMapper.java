package com.prolog.eis.sc.dao.container;

import com.prolog.eis.model.sc.containertask.ContainerTaskDetail;
import com.prolog.framework.dao.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * @Author clarence_she
 * @Date 2021/8/18
 **/
public interface ScContainerTaskDetailMapper extends BaseMapper<ContainerTaskDetail> {


    @Results(id = "containerTaskDetailMap" ,
            value = {
            @Result(property = "id",column = "id"),
            @Result(property = "containerTaskId",column = "container_task_id"),
                    @Result(property = "upperSystemTaskDeailId",column = "upper_system_task_deail_id"),
                    @Result(property = "containerNo",column = "container_no"),
                    @Result(property = "status",column = "status"),
                    @Result(property = "sourceArea",column = "source_area"),
                    @Result(property = "sourceLocation",column = "source_location"),
                    @Result(property = "targetArea",column = "target_area"),
                    @Result(property = "targetLocation",column = "target_location"),
                    @Result(property = "createTime",column = "create_time"),
                    @Result(property = "detailStartTime",column = "detail_start_time"),
                    @Result(property = "detailFinishTime",column = "detail_finish_time")})
    @Select("select ctd.* from container_task_detail ctd \n" +
            "inner join container_task ct on ct.id = ctd.container_task_id\n" +
            "where ctd.id not in (select cit.task_id from carry_interface_task cit )\n" +
            "and ctd.`status` = 0")
    List<ContainerTaskDetail> findReadIssueTask();

    @ResultMap("containerTaskDetailMap")
    @Select("select ctd.* from container_task_detail ctd where ctd.id in (${ids})")
    List<ContainerTaskDetail> findByIdStr(@Param("ids") String ids);

    @Update("update container_task_detail ctd \n" +
            "set ctd.`status` = 2 ,ctd.detail_finish_time = #{date},where ctd.id in (${ids})")
    long updateBatchByIds(@Param("ids")String containerTaskDetailListIdString,@Param("date") Date date);

    @Insert("insert into container_task_detail_his select * from container_task_detail ctd where ctd.container_task_id = #{containerTaskId}")
    long toHistory(@Param("containerTaskId") Integer containerTaskId);
}
