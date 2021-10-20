package com.prolog.eis.bc.dao.inbound;

import com.prolog.eis.bc.facade.dto.inbound.InboundTaskReportHisDto;
import com.prolog.eis.bc.facade.vo.inbound.InboundTaskReportHisVo;
import com.prolog.eis.core.model.biz.inbound.InboundTaskReportHis;
import com.prolog.framework.dao.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
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
public interface InboundTaskReportHisMapper extends BaseMapper<InboundTaskReportHis> {

    /**
     * 根据参数查询
     * @param dto
     * @return
     */
    @Select({"<script>" +
            "select t.* \r\n" +
            "from biz_eis_inbound_task_rp_his t \r\n" +
            "where 1 = 1 \r\n" +
            "<if test='dto.upperSystemTaskId!=null and dto.upperSystemTaskId!=\"\"'> \r\n" +
            "   and t.upper_system_task_id LIKE CONCAT('%',#{dto.upperSystemTaskId},'%') \r\n" +
            "</if> \r\n" +
            "<if test='dto.inboundTaskId!=null and dto.inboundTaskId!=\"\"'> \r\n" +
            "   and t.inbound_task_id = #{dto.inboundTaskId} \r\n" +
            "</if> \r\n" +
            "<if test='dto.createTimeFrom!=null'> \r\n" +
            "   and date(t.create_time) >= #{dto.createTimeFrom} \r\n" +
            "</if> \r\n" +
            "<if test='dto.createTimeTo!=null'> \r\n" +
            "   and t.create_time &lt;= #{dto.createTimeTo} \r\n" +
            "</if> \r\n" +
            "order by t.create_time \r\n" +
            "</script>"})
    @Results(id = "inboundTaskReportHisVoMap", value = {
            @Result(property = "upperSystemTaskId", column = "upper_system_task_id"),
            @Result(property = "inboundTaskId", column = "inbound_task_id"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "reportTime", column = "report_time"),
            @Result(property = "errorMsg", column = "error_msg")
    })
    List<InboundTaskReportHisVo> findByParam(@Param("dto") InboundTaskReportHisDto dto);

    /**
     * 转历史
     *
     * @param id
     * @return
     */
    @Insert("insert into biz_eis_inbound_task_rp_his select * from biz_eis_inbound_task_rp where id = #{id}")
    long toHistory(@Param("id") String id);
}
