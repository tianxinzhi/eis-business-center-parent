package com.prolog.eis.bc.service.inbound;

import com.prolog.eis.bc.facade.dto.inbound.InboundTaskDto;
import com.prolog.eis.bc.facade.dto.inbound.InboundTaskHisDto;
import com.prolog.eis.bc.facade.vo.inbound.InboundTaskHisVo;
import com.prolog.eis.bc.facade.vo.inbound.InboundTaskVo;
import com.prolog.eis.core.model.biz.inbound.InboundTask;
import com.prolog.eis.inter.dto.mcs.ZxMcsInBoundResponseDto;
import com.prolog.framework.core.pojo.Page;

import java.util.List;

/**
 * @author: wuxl
 * @create: 2021-10-18 17:01
 * @Version: V1.0
 */
public interface InboundTaskService {

    /**
     * 前端分页查询
     *
     * @param dto
     * @return
     */
    Page<InboundTaskVo> listInboundTaskByPage(InboundTaskDto dto);

    /**
     * 前端分页查询
     *
     * @param dto
     * @return
     */
    Page<InboundTaskHisVo> listInboundTaskHisByPage(InboundTaskHisDto dto);

    /**
     * 入库任务单查询
     *
     * @param dto
     * @return
     */
    List<InboundTaskVo> listInboundTask(InboundTaskDto dto);

    /**
     * 任务取消
     *
     * @param dto
     */
    void cancelTask(InboundTask dto) throws Exception;

    /**
     * 转历史
     *
     * @param inboundTask
     */
    void toHistory(InboundTask inboundTask) throws Exception;

    /**
     * 入库申请（指定容器）
     *
     * @param dto
     * @throws Exception
     */
    void applyContainer(ZxMcsInBoundResponseDto dto) throws Exception;

    /**
     * 生成入库任务单
     *
     * @param vo
     */
    void createInboundTask(InboundTaskVo vo);
}
