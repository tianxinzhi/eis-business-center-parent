package com.prolog.eis.bc.service.dispatch.impl;

import com.prolog.eis.bc.facade.dto.outbound.WholeOutTaskContainerDto;
import com.prolog.eis.bc.service.dispatch.OutWholeService;
import com.prolog.eis.bc.service.dispatch.datainit.OutboundWholeDataInitService;
import com.prolog.eis.bc.service.dispatch.strategy.OutboundStrategyContext;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategyTargetStationConfig;
import com.prolog.upcloud.base.strategy.domain.core.Strategy;
import com.prolog.upcloud.base.strategy.dto.StrategyDTO;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.OutboundDataSourceDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.OutboundStrategyDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.OutTaskAlgorithmDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.WholeOutContainerDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.WholeOutStrategyResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/10/22
 **/
@Service
public class OutWholeServiceImpl implements OutWholeService {

    @Autowired
    private OutboundWholeDataInitService outboundWholeDataInitService;

    @Override
    public void outContainer(WholeOutTaskContainerDto wholeOutTaskContainerDto, StrategyDTO data, OutboundStrategyTargetStationConfig outboundStrategyTargetStationConfig) throws Exception {
        List<OutTaskAlgorithmDto> outTaskAlgorithmDtoList = wholeOutTaskContainerDto.getOutTaskAlgorithmDtoList();
        for (OutTaskAlgorithmDto outTaskAlgorithmDto : outTaskAlgorithmDtoList) {
            OutboundDataSourceDto outboundDataSourceDto = new OutboundDataSourceDto();
            WholeOutContainerDto wholeOutContainerDto = new WholeOutContainerDto();
            wholeOutContainerDto.setInvStockAlgorithmDtoList(wholeOutContainerDto.getInvStockAlgorithmDtoList());
            wholeOutContainerDto.setOutTaskAlgorithmDto(outTaskAlgorithmDto);
            wholeOutContainerDto.setOriginX(outboundStrategyTargetStationConfig.getX());
            wholeOutContainerDto.setOriginY(outboundStrategyTargetStationConfig.getY());
            outboundDataSourceDto.setWholeOutContainerDto(wholeOutContainerDto);
            Map<String, String> strategyType = new HashMap<>();
            strategyType.put("strategyType", "wholeOut");
            OutboundStrategyContext outboundStrategyContext = new OutboundStrategyContext(outboundWholeDataInitService);
            OutboundStrategyDto outboundStrategyDto = new OutboundStrategyDto();
            outboundStrategyDto.setStrategyType(strategyType);
            outboundStrategyDto.setCondition(wholeOutTaskContainerDto);
            Strategy strategy = data.createStrategy();
            strategy.execute(outboundStrategyContext);
            OutboundStrategyDto outboundStrategyDto1 = (OutboundStrategyDto) outboundStrategyContext.getStrategyData();
            List<WholeOutStrategyResultDto> result = (List<WholeOutStrategyResultDto>) outboundStrategyDto1.getResult();
        }
    }
}
