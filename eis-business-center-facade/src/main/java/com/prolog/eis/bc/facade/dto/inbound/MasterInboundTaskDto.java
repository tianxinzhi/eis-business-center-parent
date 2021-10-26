package com.prolog.eis.bc.facade.dto.inbound;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: wuxl
 * @create: 2021-10-18 17:06
 * @Version: V1.0
 */
@Data
public class MasterInboundTaskDto {

    @ApiModelProperty("上游系统任务单id")
    private String upperSystemTaskId;

    @ApiModelProperty("容器号")
    private String containerNo;

    @ApiModelProperty("容器类型(-1空托剁、0非整托、1整托)")
    private int containerType;

    @ApiModelProperty("业务属性")
    private String businessProperty;

    @ApiModelProperty("子容器集合")
    private List<MasterInboundTaskSubDto> subList;
}
