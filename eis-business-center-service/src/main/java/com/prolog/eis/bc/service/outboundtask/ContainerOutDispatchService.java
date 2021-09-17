package com.prolog.eis.bc.service.outboundtask;

import com.prolog.eis.bc.facade.vo.OutboundStrategyConfigVo;
import com.prolog.eis.component.algorithm.composeorder.entity.StationDto;
import com.prolog.eis.component.algorithm.composeorder.entity.WarehouseDto;

/** 出库调度
 * @Author clarence_she
 * @Date 2021/9/13
 **/
public interface ContainerOutDispatchService {

    /**
     * 为拣选单出库
     * @param warehouseDto
     * @return
     */
    boolean outContainerForPickingOrder(WarehouseDto warehouseDto, OutboundStrategyConfigVo outboundStrategyConfigVo);


    boolean outContainerForItemId(StationDto station, String outItemId, OutboundStrategyConfigVo config)throws Exception;
}
