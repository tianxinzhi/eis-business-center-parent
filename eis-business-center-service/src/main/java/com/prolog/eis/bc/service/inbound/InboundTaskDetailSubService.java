package com.prolog.eis.bc.service.inbound;

import com.prolog.eis.core.model.biz.inbound.InboundTaskDetailSub;
import com.prolog.eis.core.model.biz.inbound.InboundTaskDetailSubHis;

import java.util.List;

/**
 * @author: wuxl
 * @create: 2021-10-18 17:01
 * @Version: V1.0
 */
public interface InboundTaskDetailSubService {

    /**
     * 根据任务单明细ID查询所有子任务
     *
     * @param inboundTaskDetailId
     * @return
     */
    List<InboundTaskDetailSub> listInboundTaskDetailSubByParam(String inboundTaskDetailId);

    /**
     * 根据任务单明细ID查询所有子任务
     *
     * @param inboundTaskDetailId
     * @return
     */
    List<InboundTaskDetailSubHis> listInboundTaskDetailSubHisByParam(String inboundTaskDetailId);

    /**
     * 转历史
     *
     * @param id
     */
    void toHistory(String id) throws Exception;

    /**
     * 批量存
     *
     * @param subList
     */
    long saveBatch(List<InboundTaskDetailSub> subList);
}
