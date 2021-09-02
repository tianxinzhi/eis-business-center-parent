package com.prolog.eis.sc.dao.container;

import com.prolog.eis.model.route.task.CarryInterfaceTaskCallback;
import com.prolog.eis.model.sc.containertask.ContainerTask;
import com.prolog.framework.dao.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @Author clarence_she
 * @Date 2021/8/18
 **/
public interface ScContainerTaskMapper extends BaseMapper<ContainerTask> {

    @Results(id = "carryInterfaceTaskCallbackMap" ,
            value = {
                    @Result(property = "taskId",column = "task_id"),
                    @Result(property = "containerNo",column = "container_no"),
                    @Result(property = "palletNo",column = "pallet_no"),
                    @Result(property = "taskType",column = "task_type"),
                    @Result(property = "startRegion",column = "start_region"),
                    @Result(property = "startLocation",column = "start_location"),
                    @Result(property = "endRegion",column = "end_region"),
                    @Result(property = "endLocation",column = "end_location"),
                    @Result(property = "isChangeTask",column = "is_change_task"),
                    @Result(property = "cleanTaskStatus",column = "clean_task_status"),
                    @Result(property = "taskStatus",column = "task_status"),
                    @Result(property = "priority",column = "priority"),
                    @Result(property = "businessType",column = "business_type"),
                    @Result(property = "businessDescribe",column = "business_describe"),
                    @Result(property = "businessProperty",column = "business_property"),
                    @Result(property = "height",column = "height"),
                    @Result(property = "weight",column = "weight"),
                    @Result(property = "isPreCall",column = "is_pre_call"),
                    @Result(property = "createTime",column = "create_time"),})
    @Select("select t.* from carry_interface_task_callback t where t.task_status = 2")
    List<CarryInterfaceTaskCallback> findUnFinishTasks();

    @Insert("insert into container_task_his select * from container_task ct where ct.id = #{containerTaskId}")
    void toHistory(@Param("containerTaskId") Integer containerTaskId);
}
