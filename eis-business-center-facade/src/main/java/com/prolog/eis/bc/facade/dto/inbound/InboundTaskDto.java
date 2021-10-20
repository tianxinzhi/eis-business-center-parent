package com.prolog.eis.bc.facade.dto.inbound;

import com.prolog.eis.core.model.biz.inbound.InboundTask;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author: wuxl
 * @create: 2021-10-18 17:06
 * @Version: V1.0
 */
@Data
public class InboundTaskDto extends InboundTask {

    @ApiModelProperty("创建时间-开始时间")
    private Date createTimeFrom;

    @ApiModelProperty("创建时间-结束时间")
    private Date createTimeTo;

    @ApiModelProperty("当前页")
    private Integer pageNum;

    @ApiModelProperty("每页行数")
    private Integer pageSize;
}
