package com.prolog.eis.bc.facade.dto.osr;

import com.prolog.upcloud.base.inventory.vo.EisInvContainerStoreVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: txz
 * @Date: 2021/9/26 10:17
 * @Desc: 拆单策略算法返回值
 */
@Data
@ApiModel(description = "拆单策略算法返回值")
public class SplitStrategyResultDto {

    @ApiModelProperty("剩余订单数量")
    private Double remainOrderQty;
    @ApiModelProperty("容器超出的容量")
    private Double containerOverQty;
    @ApiModelProperty("容器编号")
    private List<String> containerNos;
    @ApiModelProperty("子容器编号")
    private List<List<String>> subContainerNos;
    @ApiModelProperty("子容器数量")
    private List<List<Double>> subConQtys;
    @ApiModelProperty("剩余可用容器")
    private List<EisInvContainerStoreVo> remainContainerStoreVos;
}
