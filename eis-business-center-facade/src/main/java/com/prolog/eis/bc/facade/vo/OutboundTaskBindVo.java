package com.prolog.eis.bc.facade.vo;

import com.prolog.eis.core.model.biz.outbound.OutboundTaskBind;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskBindDetail;
import lombok.Data;

import java.util.List;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/9/29
 **/
@Data
public class OutboundTaskBindVo extends OutboundTaskBind {

    /**
     * 容器绑定任务下的明细
     */
    private List<OutboundTaskBindDetail> outboundTaskBindDetailList;

    /**
     * 明细数量
     */
    private int detailSize;
}
