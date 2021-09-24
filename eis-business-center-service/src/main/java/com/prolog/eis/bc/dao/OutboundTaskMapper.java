package com.prolog.eis.bc.dao;

import org.springframework.stereotype.Repository;

import com.prolog.eis.core.model.biz.outbound.OutboundTask;
import com.prolog.framework.dao.mapper.BaseMapper;

/**
 * 出库任务Mapper
 * @author 金总
 *
 */
@Repository
public interface OutboundTaskMapper extends BaseMapper<OutboundTask> {

}
