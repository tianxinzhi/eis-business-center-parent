package com.prolog.eis.bc.facade.vo.inbound;

import com.prolog.eis.core.model.biz.inbound.InboundTaskDetailHis;
import com.prolog.eis.core.model.biz.inbound.InboundTaskDetailSubHis;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: wuxl
 * @create: 2021-10-18 17:14
 * @Version: V1.0
 */
@Data
public class InboundTaskDetailHisVo extends InboundTaskDetailHis {

    @ApiModelProperty("入库任务单明细子任务历史集合")
    private List<InboundTaskDetailSubHis> inboundTaskDetailSubHisList;

    @ApiModelProperty("子任务历史数量")
    private int subSize;
}
