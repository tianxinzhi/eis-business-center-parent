package com.prolog.eis.bc.dao;

import org.springframework.stereotype.Repository;

import com.prolog.eis.core.model.biz.outbound.PickingOrder;
import com.prolog.framework.dao.mapper.BaseMapper;

/**
 * 拣选单Mapper
 * @author 金总
 *
 */
@Repository
public interface PickingOrderMapper extends BaseMapper<PickingOrder> {

}
