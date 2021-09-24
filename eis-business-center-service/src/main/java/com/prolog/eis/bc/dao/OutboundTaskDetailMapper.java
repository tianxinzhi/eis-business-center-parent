package com.prolog.eis.bc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.prolog.eis.core.model.biz.outbound.OutboundTaskDetail;
import com.prolog.framework.dao.mapper.BaseMapper;

public interface OutboundTaskDetailMapper extends BaseMapper<OutboundTaskDetail> {

    @Select("<script>"
            + "    select id id, out_task_id outTaskId, lot_id lotId, item_id itemId, plan_num planNum "
            + "        actual_num actualNum, is_finish isFinish, "
            + "        is_short_picking isShortPicking, priority priority, expire_date expireDate,"
            + "        create_time createTime, start_time startTime, finish_time finishTime "
            + "    from biz_eis_out_task_dt" + "    where 1 = 1 "
            + "    <if test='outTaskIdList != null and outTaskIdList.size > 0'>"
            + "        and out_task_id in "
            + "        <foreach collection='outTaskIdList' item='item' index='index' open='(' close=')' separator=','>"
            + "            #{item,jdbcType=VARCHAR}"
            + "        </foreach>"
            + "    </if>"
            + "    <if test='lotId != null'>" 
            + "        and lot_id = #{lotId,jdbcType=VARCHAR}" 
            + "    </if>"
            + "    <if test='itemId != null'>" 
            + "        and item_id = #{itemId,jdbcType=VARCHAR}" 
            + "    </if>"
            + "    <if test='isFinish != null'>" 
            + "        and is_finish = #{isFinish,jdbcType=INTEGER}" 
            + "    </if>"
            + "    <if test='isShortPicking != null'>" 
            + "        and is_short_picking = #{isShortPicking,jdbcType=INTEGER}" 
            + "    </if>"
            + "    order by create_time desc"
            + "</script>")
    List<OutboundTaskDetail> find(
            @Param("outTaskIdList") List<String> outTaskIdList,
            @Param("lotId") String lotId, @Param("itemId") String itemId,
            @Param("isFinish") Integer isFinish,
            @Param("isShortPicking") Integer isShortPicking);
}
