package com.prolog.eis.bc.dao;

import com.prolog.eis.core.model.biz.outbound.OutboundTaskBindDetail;
import com.prolog.framework.dao.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author clarence_she
 * @Date 2021/9/14
 **/
@Repository
public interface OutboundTaskBindDetailMapper extends BaseMapper<OutboundTaskBindDetail> {
    @Select("<script>"
            + "    select sum(binding_num) bindingNum, item_id itemId, lot_id lotId, out_task_detail_id outTaskDetailId "
            + "    from biz_eis_out_task_bind_dt" + "    where 1 = 1 "
            + "    <if test='outTaskDetailIdList != null and outTaskDetailIdList.size > 0'>"
            + "        and out_task_detail_id in "
            + "        <foreach collection='outTaskDetailIdList' item='item' index='index' open='(' close=')' separator=','>"
            + "            #{item,jdbcType=VARCHAR}"
            + "        </foreach>"
            + "    </if>"
            + "    group by out_task_detail_id"
            + "</script>")
    List<OutboundTaskBindDetail> findSumBindingNumGroupByOutTaskDetailId(
            @Param("outTaskDetailIdList") List<String> outTaskDetailIdList);

    @Select("<script>"
            + "    select sum(binding_num) bindingNum, lot_id lotId "
            + "    from biz_eis_out_task_bind_dt"
            + "    group by lot_id"
            + "</script>")
    List<OutboundTaskBindDetail> findSumBindingNumGroupByLotId();

    @Select("<script>"
            + "    select sum(binding_num) bindingNum, item_id itemId "
            + "    from biz_eis_out_task_bind_dt"
            + "    group by item_id"
            + "</script>")
    List<OutboundTaskBindDetail> findSumBindingNumGroupByItemId();
}
