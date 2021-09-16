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
public class GoodsInventoryWarnDefineDto {

    @ApiModelProperty("当前页")
    private Integer pageNum;

    @ApiModelProperty("每页行数")
    private Integer pageSize;

}
