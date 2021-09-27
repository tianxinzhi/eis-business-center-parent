package com.prolog.eis.bc.dao.ssc;

import com.prolog.eis.bc.facade.dto.product.GoodsInventoryInfoDto;
import com.prolog.eis.bc.facade.dto.product.GoodsInventoryWarnDefineDto;
import com.prolog.eis.core.model.biz.route.ContainerLocation;
import com.prolog.eis.core.model.ctrl.outbound.OutboundSplitStrategyConfig;
import com.prolog.framework.dao.mapper.BaseMapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: txz
 * @Date: 2021/9/23 17:01
 * @Desc: 出库拆单策略mapper
 */
@Repository
public interface OutboundSplitStrategyConfigMapper extends BaseMapper<OutboundSplitStrategyConfig> {

}
