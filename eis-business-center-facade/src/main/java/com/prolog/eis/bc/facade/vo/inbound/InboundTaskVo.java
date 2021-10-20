package com.prolog.eis.bc.facade.vo.inbound;

import com.prolog.eis.core.model.biz.inbound.InboundTask;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: wuxl
 * @create: 2021-10-18 17:14
 * @Version: V1.0
 */
@Data
public class InboundTaskVo extends InboundTask {

    @ApiModelProperty("入库任务单明细集合")
    private List<InboundTaskDetailVo> inboundTaskDetailVoList;

    @ApiModelProperty("明细数量")
    private int detailSize;
}
