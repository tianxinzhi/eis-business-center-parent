package com.prolog.eis.bc.dao.inbound;

import com.prolog.eis.core.model.biz.inbound.InboundTaskDetailHis;
import com.prolog.framework.dao.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author: wuxl
 * @create: 2021-10-18 16:28
 * @Version: V1.0
 */
@Repository
public interface InboundTaskDetailHisMapper extends BaseMapper<InboundTaskDetailHis> {

    /**
     * 转历史
     *
     * @param id
     * @return
     */
    @Insert("insert into biz_eis_inbound_task_dt_his select * from biz_eis_inbound_task_dt where id = #{id}")
    long toHistory(@Param("id") String id);
}
