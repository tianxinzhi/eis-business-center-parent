package com.prolog.eis.bc.service.inbound;

import com.prolog.eis.bc.facade.vo.inbound.InboundTaskDetailHisVo;
import com.prolog.eis.bc.facade.vo.inbound.InboundTaskDetailVo;
import com.prolog.eis.core.model.biz.inbound.InboundTaskDetail;

import java.util.List;
import java.util.Map;

/**
 * @author: wuxl
 * @create: 2021-10-18 17:01
 * @Version: V1.0
 */
public interface InboundTaskDetailService {

    /**
     * 查询所有明细
     *
     * @return
     */
    List<InboundTaskDetailVo> listInboundTaskDetailByParam(String inboundTaskId);

    /**
     * 查询所有明细
     *
     * @return
     */
    List<InboundTaskDetailHisVo> listInboundTaskDetailHisByParam(String inboundTaskId);

    /**
     * 转历史
     *
     * @param inboundTaskDetail
     */
    void toHistory(InboundTaskDetail inboundTaskDetail) throws Exception;

    /**
     * 批量存
     *
     * @param detailList
     */
    long saveBatch(List<InboundTaskDetail> detailList);

    /**
     * 单笔存
     *
     * @param detail
     */
    long save(InboundTaskDetail detail);

    /**
     * 修改
     *
     * @param id
     * @param map
     */
    long updateById(String id, Map<String, Object> map);
}
