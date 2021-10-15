package com.prolog.eis.bc.dao;

import com.prolog.eis.bc.facade.dto.osr.OrderPoolMixDto;
import com.prolog.eis.core.model.biz.outbound.OutboundTask;
import com.prolog.framework.dao.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 出库任务Mapper
 * @author 金总
 *
 */
@Repository
public interface OutboundTaskMapper extends BaseMapper<OutboundTask> {

    /**
     * 获取订单池未下发任务单
     * @return
     */
    @Results({
            @Result(column = "id",property = "outTaskId"),
            @Result(column = "store_matching_strategy",property = "matchStrategy"),
            @Result(column = "type_no",property = "typeNo"),
            })
//    @Select("select ot.id,t1.type_no,t1.store_matching_strategy \n" +
//            "from ctrl_eis_out_stg_cfg t1\n" +
//            "inner JOIN biz_eis_out_task ot\n" +
//            "on ot.outbound_task_type_no = t1.type_no\n" +
//            "where t1.type_no in (#{typeNos}) and ot.state = 0 and ot.out_task_smy_id is null  \n" +
//            "and ot.picking_order_id is null and ot.order_pool_id is not null ")
    @Select("<script>select ot.id,t1.type_no,t1.store_matching_strategy,t3.group_no \n" +
            "from ctrl_eis_out_stg_cfg t1\n" +
            "inner join biz_eis_order_pool t2\n" +
            "on t1.type_no = t2.type_no\n" +
            "LEFT JOIN biz_eis_order_pool_dt t3\n" +
            "on t3.order_pool_id = t2.id\n" +
            "left JOIN biz_eis_out_task ot\n" +
            "on ot.id = t3.out_task_id\n" +
            "where t1.out_model = #{outModel} and ot.state = 0 and ot.out_task_smy_id is null  \n" +
            "and ot.picking_order_id is null and ot.order_pool_id is not null</script>")
    List<OrderPoolMixDto> getOrderPoolNotStart(@Param("outModel") String outModel);

    /**
     * 获取未到达最大数量的汇总单
     * @return
     */
    @Results({
            @Result(column = "smyId",property = "smyId"),
            @Result(column = "type_no",property = "typeNo"),
            @Result(column = "taskId",property = "outTaskId"),
            @Result(column = "max_order_num",property = "maxOrderNum"),
            @Result(column = "outTaskNum",property = "outTaskNum"),
            @Result(column = "store_matching_strategy",property = "matchStrategy"),
    })
    @Select("<script>select smy.id smyId,ot.id taskId,t1.max_order_num,t1.type_no,t1.store_matching_strategy,\n" +
            " count(ot.id) over(PARTITION by smy.id) outTaskNum \n" +
            "from ctrl_eis_out_stg_cfg t1\n" +
            "inner join biz_eis_out_smy_order smy\n" +
            "on smy.type_no = t1.type_no\n" +
            "inner JOIN biz_eis_out_task ot\n" +
            "on ot.out_task_smy_id = smy.id\n" +
            "where t1.out_model = #{outModel} and smy.state = 0 and ot.state = 0 and ot.out_task_smy_id is not null \n" +
            "and ot.picking_order_id is null</script> ") //未按分组统计
    List<OrderPoolMixDto> getNotFullSummaryOrder(@Param("outModel") String outModel);

    /**
     * 获取未到达最大数量的汇总单
     * @return
     */
    @Results({
            @Result(column = "smyId",property = "smyId"),
            @Result(column = "type_no",property = "typeNo"),
            @Result(column = "taskId",property = "outTaskId"),
            @Result(column = "max_order_num",property = "maxOrderNum"),
            @Result(column = "outTaskNum",property = "outTaskNum"),
            @Result(column = "store_matching_strategy",property = "matchStrategy"),
    })
    @Select("<script>select smy.id smyId,ot.id taskId,t1.max_order_num,t1.type_no,\n" +
            "t1.store_matching_strategy,oddt.group_no,\n" +
            "count(ot.id) over(PARTITION by smy.id,oddt.group_no) outTaskNum \n" +
            "from ctrl_eis_out_stg_cfg t1\n" +
            "inner join biz_eis_out_smy_order smy\n" +
            "on smy.type_no = t1.type_no\n" +
            "inner JOIN biz_eis_out_task ot\n" +
            "on ot.out_task_smy_id = smy.id\n" +
            "INNER JOIN biz_eis_order_pool od\n" +
            "on ot.order_pool_id = od.id\n" +
            "INNER JOIN biz_eis_order_pool_dt oddt\n" +
            "on oddt.order_pool_id = od.id\n" +
            "where t1.out_model = #{outModel} and smy.state = 0 and ot.state = 0 and ot.out_task_smy_id is not null \n" +
            "and ot.picking_order_id is null and ot.order_pool_id is not null</script>") //按分组统计
    List<OrderPoolMixDto> getNotFullSummaryOrderByGroup(@Param("outModel") String outModel);
}
