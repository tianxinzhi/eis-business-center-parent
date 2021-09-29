package com.prolog.eis.bc.facade.dto.businesscenter;

import com.prolog.eis.core.model.biz.outbound.OutboundTaskBindHis;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class OutBindingTaskHisDto  extends OutboundTaskBindHis {
    @ApiModelProperty("任务明细数")
    private int detailCount;

    @ApiModelProperty("创建时间-开始时间")
    private Date createTimeFrom;

    @ApiModelProperty("创建时间-结束时间")
    private Date createTimeTo;

    @ApiModelProperty("当前页")
    private Integer pageNum;

    @ApiModelProperty("每页行数")
    private Integer pageSize;
}
