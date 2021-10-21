package com.prolog.eis.bc.service.dispatch.strategy;

import com.prolog.eis.bc.service.dispatch.datainit.OutboundWholeDataInitService;
import com.prolog.framework.utils.StringUtils;
import com.prolog.upcloud.base.strategy.domain.core.SearchFactor;
import com.prolog.upcloud.base.strategy.domain.core.StrategyContext;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.OutboundDataSourceDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.OutboundStrategyDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/10/21
 **/
public class OutboundStrategyContext extends StrategyContext<OutboundDataSourceDto> {

    private final OutboundWholeDataInitService outboundWholeDataInitService;

    public OutboundStrategyContext(OutboundWholeDataInitService outboundWholeDataInitService){
        this.outboundWholeDataInitService = outboundWholeDataInitService;
    }

    @Override
    public boolean completed() {
        return this.getStrategyData().isCompleted();
    }

    @Override
    public Object getMatchFieldValue(String fieldName) {
        OutboundStrategyDto strategyData = (OutboundStrategyDto) super.getStrategyData();
        if(strategyData!=null && strategyData.getStrategyType()!=null){
            String s = strategyData.getStrategyType().get(fieldName);
            if(!StringUtils.isBlank(s)) {
                return s;
            }
        }
        return null;
    }

    @Override
    public List<OutboundDataSourceDto> search(List<SearchFactor> searchFactors) {
        List<OutboundDataSourceDto> outboundDataSourceDtoList = new ArrayList<>();
        OutboundStrategyDto strategyData = (OutboundStrategyDto) super.getStrategyData();
        String strategyType = strategyData.getStrategyType().get("strategyType");
        outboundDataSourceDtoList.add((OutboundDataSourceDto)strategyData.getCondition());
        return outboundDataSourceDtoList;
    }
}
