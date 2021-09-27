package com.prolog.eis.bc.facade.dto.osr;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 出库汇总任务单
 * @author txz
 * @date 2021/9/23 09:28
 */
@Data
@ApiModel
@AllArgsConstructor
public class OutSummaryOrderInfoDto {

    @ApiModelProperty("订单类型")//匹配策略编号
    @NotNull(message = "订单类型编号不可为空")
    private String orderType;

    @ApiModelProperty("原订单id")
    @NotNull(message = "原订单id不可为空")
    private String oldOrderId;

    @ApiModelProperty("订单号")
    @NotNull(message = "订单号不可为空")
    private String orderNo;

    @ApiModelProperty("优先级")
    private String priority;

    @ApiModelProperty("整容器拼装属性")
    private String  allContainerMixAttr;

    @ApiModelProperty("同步性出库时间")
    private String  syncOutTime;

    @ApiModelProperty("汇总单明细")
    @Valid
    private List<OutSummaryOrderDetailInfoDto> dtls;

    /**
     * 出库汇总任务单明细
     */
    @Data
    @ApiModel
    public static class OutSummaryOrderDetailInfoDto{

        @ApiModelProperty("商品名称")
        @NotNull(message = "商品名称不可为空")
        private String  itemId;

        @ApiModelProperty("商品批次")
        @NotNull(message = "商品批次不可为空")
        private String  lotId;

        @ApiModelProperty("库存数量")
        private Integer  storeQty;

        @ApiModelProperty("订单数量")
        @Min(value = 1,message = "订单数量不可为空")
        private Double  orderQty;
    }
}
