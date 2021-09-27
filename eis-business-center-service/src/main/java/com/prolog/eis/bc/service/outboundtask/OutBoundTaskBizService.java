package com.prolog.eis.bc.service.outboundtask;

import com.prolog.eis.bc.facade.vo.OutboundStrategyConfigVo;
import com.prolog.eis.component.algorithm.composeorder.entity.WarehouseDto;

/**
 * 出货单业务Service
 */
public interface OutBoundTaskBizService {

    /**
     * 获取PickingOutModel模式下的仓库对象
     * @param config 出货策略配置
     * @return
     */
    WarehouseDto getWarehouseByPickingOrderOutModel(OutboundStrategyConfigVo config);
}
