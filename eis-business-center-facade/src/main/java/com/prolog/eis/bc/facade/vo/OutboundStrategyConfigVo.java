package com.prolog.eis.bc.facade.vo;

import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategyConfig;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategySourceAreaConfig;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategyTargetStationConfig;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/9/15
 **/
@Data
@ApiModel
public class OutboundStrategyConfigVo extends OutboundStrategyConfig {

    @ApiModelProperty("出库策略的源区域集合")
    private List<OutboundStrategySourceAreaConfig> outboundStrategySourceAreaConfigList;

    @ApiModelProperty("出库策略的目标区域集合")
    private List<OutboundStrategyTargetStationConfig> outboundStrategyTargetStationConfigList;
}
