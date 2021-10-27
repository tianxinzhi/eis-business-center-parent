package com.prolog.eis.bc.service.dispatch;

import com.prolog.eis.bc.facade.dto.outbound.WholeStationDto;
import com.prolog.eis.bc.facade.vo.OutboundStrategyConfigVo;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.OutTaskAlgorithmDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.WholeOutStrategyResultDto;

import java.util.List;

/**
 * @Author clarence_she
 * @Date 2021/10/27
 **/
public interface DispatchDataGenerateService {

    /**
     * 生成数据
     * @param result
     * @param outTaskAlgorithmDto
     * @param wholeStationDto
     * @param outboundStrategyConfigVo
     * @throws Exception
     */
    void generateData(List<WholeOutStrategyResultDto> result, OutTaskAlgorithmDto outTaskAlgorithmDto, WholeStationDto wholeStationDto, OutboundStrategyConfigVo outboundStrategyConfigVo)throws Exception;

}
