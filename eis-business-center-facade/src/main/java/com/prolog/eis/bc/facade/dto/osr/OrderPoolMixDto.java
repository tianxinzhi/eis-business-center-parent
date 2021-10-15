package com.prolog.eis.bc.facade.dto.osr;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: txz
 * @Date: 2021/10/12 13:52
 * @Desc:
 */
@Data
public class OrderPoolMixDto {

    @ApiModelProperty("任务单id")
    private String outTaskId;
    @ApiModelProperty("出库模式")
    private String outModel;
    @ApiModelProperty("按品或批统计的商品数量")
    private int matchStrategy;
    @ApiModelProperty("汇总单id")
    private String smyId;
    @ApiModelProperty("最大组单数量")
    private int maxOrderNum;
    @ApiModelProperty("汇总单拥有的任务单数量")
    private int outTaskNum;
    @ApiModelProperty("策略类型编号")
    private String typeNo;
    @ApiModelProperty("组编号")
    private String groupNo;
}
