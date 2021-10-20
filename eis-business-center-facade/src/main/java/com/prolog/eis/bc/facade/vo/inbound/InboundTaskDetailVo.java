package com.prolog.eis.bc.facade.vo.inbound;

import com.prolog.eis.core.model.biz.inbound.InboundTaskDetail;
import com.prolog.eis.core.model.biz.inbound.InboundTaskDetailSub;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: wuxl
 * @create: 2021-10-18 17:14
 * @Version: V1.0
 */
@Data
public class InboundTaskDetailVo extends InboundTaskDetail {

    @ApiModelProperty("入库任务单明细子任务集合")
    private List<InboundTaskDetailSub> inboundTaskDetailSubList;

    @ApiModelProperty("子任务数量")
    private int subSize;

    @ApiModelProperty("业务属性")
    private String businessProperty;
}
