package com.prolog.eis.bc.service.dispatch;

import com.prolog.eis.bc.facade.dto.outbound.WholeOutTaskContainerDto;
import com.prolog.eis.bc.facade.dto.outbound.WholeStationDto;
import com.prolog.eis.bc.facade.vo.OutboundStrategyConfigVo;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategyTargetStationConfig;
import com.prolog.upcloud.base.strategy.dto.StrategyDTO;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.OutTaskAlgorithmDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.WholeOutStrategyResultDto;

import java.util.List;

/**
 * @Author clarence_she
 * @Date 2021/10/22
 **/
public interface OutWholeService {

    /**
     * 执行算法
     * @param wholeStationDto
     * @param wholeOutTaskContainerDto
     * @param data
     * @param outboundStrategyTargetStationConfig
     * @param outboundStrategyConfigVo
     * @throws Exception
     */
    boolean outContainer(WholeStationDto wholeStationDto, WholeOutTaskContainerDto wholeOutTaskContainerDto, StrategyDTO data, OutboundStrategyTargetStationConfig outboundStrategyTargetStationConfig, OutboundStrategyConfigVo outboundStrategyConfigVo)throws Exception;


}
