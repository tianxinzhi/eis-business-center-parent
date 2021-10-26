package com.prolog.eis.bc.facade.dto.inbound;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: wuxl
 * @create: 2021-10-18 17:06
 * @Version: V1.0
 */
@Data
public class MasterInboundTaskSubDto {

    @ApiModelProperty("子容器号")
    private String containerSubNo;

    @ApiModelProperty("商品id")
    private String itemId;

    @ApiModelProperty("批次id")
    private String lotId;

    @ApiModelProperty("数量")
    private float qty;
}
