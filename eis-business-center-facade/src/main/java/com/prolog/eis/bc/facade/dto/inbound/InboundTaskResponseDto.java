package com.prolog.eis.bc.facade.dto.inbound;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: wuxl
 * @create: 2021-10-18 17:06
 * @Version: V1.0
 */
@Data
public class InboundTaskResponseDto {

    @ApiModelProperty("任务号")
    private String taskId;

    @ApiModelProperty("容器号")
    private String containerNo;

    @ApiModelProperty("容器类型(-1空托剁、0非整托、1整托)")
    private String containerType;

    @ApiModelProperty("商品id")
    private String itemId;

    @ApiModelProperty("批次id")
    private String lotId;

    @ApiModelProperty("入库数量")
    private float qty;
}
