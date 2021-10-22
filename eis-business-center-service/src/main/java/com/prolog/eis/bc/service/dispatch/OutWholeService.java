package com.prolog.eis.bc.service.dispatch;

import com.prolog.eis.bc.facade.dto.outbound.WholeOutTaskContainerDto;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategyTargetStationConfig;
import com.prolog.upcloud.base.strategy.dto.StrategyDTO;

/**
 * @Author clarence_she
 * @Date 2021/10/22
 **/
public interface OutWholeService {

    void outContainer(WholeOutTaskContainerDto wholeOutTaskContainerDto, StrategyDTO data, OutboundStrategyTargetStationConfig outboundStrategyTargetStationConfig)throws Exception;
}
