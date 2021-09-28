package com.prolog.eis.bc.dao;

import com.prolog.eis.bc.facade.dto.policy.OutStgDto;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategyConfig;
import com.prolog.framework.dao.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @Author clarence_she
 * @Date 2021/9/15
 **/
@Repository
public interface OutboundStrategyConfigMapper extends BaseMapper<OutboundStrategyConfig> {

	@Select("<script>" +
            "select id,type_no as typeNo,type_name as typeName,out_model as outModel,dispatch_priority as dispatchPriority,compose_order_config as composeOrderConfig,max_order_num as maxOrderNum," +
            " max_order_volume as maxOrderVolume,store_matching_strategy as storeMatchingStrategy,outbound_expiry_date_rate as outboundExpiryDateRate,prohibit_expiry_date_rate as prohibitExpiryDateRate," +
            " clear_store_strategy as clearStoreStrategy" +
            " from ctrl_eis_out_stg_cfg" +
            " where 1=1" +
            " <if test='typeNo != null and typeNo != \"\"'>" +
            " and type_no like concat('%',#{typeNo},'%')" +
            " </if>" +
            " <if test='typeName != null and typeName != \"\"'>" +
            " and type_name like concat('%',#{typeName},'%')" +
            " </if>" +
            " <if test='outModel != null and outModel != \"\"'>" +
            " and out_model = #{outModel} " +
            " </if>" +
            "</script>")
	List<OutboundStrategyConfig> getOutStg(OutStgDto dto);
}
