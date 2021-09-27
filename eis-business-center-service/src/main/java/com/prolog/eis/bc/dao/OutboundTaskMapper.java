<<<<<<< HEAD
package com.prolog.eis.bc.dao;

import com.prolog.eis.core.model.biz.outbound.OutboundSummaryOrder;
import com.prolog.eis.core.model.biz.outbound.OutboundTask;
import com.prolog.framework.dao.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * @Author: txz
 * @Date: 2021/9/24 12:02
 * @Desc:
 */
@Repository
public interface OutboundTaskMapper extends BaseMapper<OutboundTask> {

}
=======
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
>>>>>>> 731d0307c76c19e7903dae292de004910c6bb2e8
