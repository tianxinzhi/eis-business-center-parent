package com.prolog.eis.bc.dao.inbound;

import com.prolog.eis.bc.facade.vo.inbound.InboundTaskDetailVo;
import com.prolog.eis.core.model.biz.inbound.InboundTaskDetail;
import com.prolog.framework.dao.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: wuxl
 * @create: 2021-10-18 16:28
 * @Version: V1.0
 */
@Repository
public interface InboundTaskDetailMapper extends BaseMapper<InboundTaskDetail> {

    /**
     * 根据参数查询
     *
     * @param inboundTaskId
     * @return
     */
    @Select({"<script>" +
            "select t.* \r\n" +
            "from biz_eis_inbound_task_dt t \r\n" +
            "where 1 = 1 \r\n" +
            "<if test='inboundTaskId!=null and inboundTaskId!=\"\"'> \r\n" +
            "   and t.inbound_task_id = #{inboundTaskId} \r\n" +
            "</if> \r\n" +
            "order by t.create_time \r\n" +
            "</script>"})
    @Results(id = "inboundTaskDetailVoMap", value = {
            @Result(property = "inboundTaskId", column = "inbound_task_id"),
            @Result(property = "containerNo", column = "container_no"),
            @Result(property = "containerType", column = "container_type"),
            @Result(property = "sourceArea", column = "source_area"),
            @Result(property = "sourceLocation", column = "source_location"),
            @Result(property = "portNo", column = "port_no"),
            @Result(property = "taskId", column = "task_id"),
            @Result(property = "detailStatus", column = "detail_status"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "startTime", column = "start_time"),
            @Result(property = "finishTime", column = "finish_time"),
            @Result(property = "businessProperty", column = "business_property")
    })
    List<InboundTaskDetailVo> findByParam(@Param("inboundTaskId") String inboundTaskId);
}
