package com.prolog.eis.bc.facade.dto.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author txz
 * @date 2021/9/14
 */
@Data
@ApiModel
public class GoodsInventoryInfoDto {

    @ApiModelProperty("商品id")
    private String itemId;

    @ApiModelProperty("批次id")
    private String lotId;

    @ApiModelProperty("起始点位")
    private String sourceLocation;

    @ApiModelProperty("业主")
    private String ownerName;

    @ApiModelProperty("商品名称")
    private String  itemName;

    @ApiModelProperty("商品条码")
    private String  itemCode;

    @ApiModelProperty("商品批次")
    private String  batchNum;

    @ApiModelProperty("库存数量")
    private Double  storeQty;

    @ApiModelProperty("订单数量")
    private Double  orderQty;

    @ApiModelProperty("缺货数量")
    private Double  stockoutQty;

    @ApiModelProperty("锁定数量")
    private Double  lockQty;

    @ApiModelProperty("锁定缺货数量")
    private Double  lockStockoutQty;

    @ApiModelProperty("当前页")
    private Integer pageNum;

    @ApiModelProperty("每页行数")
    private Integer pageSize;
}
