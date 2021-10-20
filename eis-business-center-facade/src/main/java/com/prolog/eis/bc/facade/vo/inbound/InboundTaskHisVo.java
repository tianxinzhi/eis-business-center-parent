package com.prolog.eis.bc.facade.vo.inbound;

import com.prolog.eis.core.model.biz.inbound.InboundTaskHis;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: wuxl
 * @create: 2021-10-18 17:14
 * @Version: V1.0
 */
@Data
public class InboundTaskHisVo extends InboundTaskHis {

    @ApiModelProperty("入库任务单明细历史集合")
    private List<InboundTaskDetailHisVo> inboundTaskDetailHisVoList;

    @ApiModelProperty("明细历史数量")
    private int detailSize;
}
