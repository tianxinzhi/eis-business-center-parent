package com.prolog.eis.bc.dao;

import com.prolog.eis.bc.facade.dto.businesscenter.BusiContainerTaskDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Repository;

import com.prolog.eis.core.model.biz.outbound.OutboundTask;
import com.prolog.framework.dao.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 出库任务Mapper
 * @author 金总
 *
 */
@Repository
public interface OutboundTaskMapper extends BaseMapper<OutboundTask> {

}
