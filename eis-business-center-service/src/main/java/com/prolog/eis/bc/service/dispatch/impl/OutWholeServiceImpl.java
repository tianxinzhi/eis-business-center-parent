package com.prolog.eis.bc.service.dispatch.impl;

import com.alibaba.fastjson.JSONObject;
import com.prolog.eis.bc.dao.OutboundTaskBindDetailMapper;
import com.prolog.eis.bc.dao.OutboundTaskBindMapper;
import com.prolog.eis.bc.dao.OutboundTaskMapper;
import com.prolog.eis.bc.facade.dto.outbound.WholeOutTaskContainerDto;
import com.prolog.eis.bc.facade.dto.outbound.WholeStationDto;
import com.prolog.eis.bc.facade.vo.OutboundStrategyConfigVo;
import com.prolog.eis.bc.feign.container.EisContainerRouteClient;
import com.prolog.eis.bc.feign.container.EisContainerStoreFeign;
import com.prolog.eis.bc.service.dispatch.DispatchDataGenerateService;
import com.prolog.eis.bc.service.dispatch.OutWholeService;
import com.prolog.eis.bc.service.dispatch.datainit.OutboundWholeDataInitService;
import com.prolog.eis.bc.service.dispatch.strategy.OutboundStrategyContext;
import com.prolog.eis.bc.service.outboundtask.OutboundTaskBindService;
import com.prolog.eis.common.util.PrologStringUtils;
import com.prolog.eis.core.model.biz.carry.CarryTask;
import com.prolog.eis.core.model.biz.outbound.OutboundTask;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskBind;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskBindDetail;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategyTargetStationConfig;
import com.prolog.eis.router.vo.ContainerLocationVo;
import com.prolog.framework.common.message.RestMessage;
import com.prolog.framework.core.exception.PrologException;
import com.prolog.framework.utils.MapUtils;
import com.prolog.framework.utils.StringUtils;
import com.prolog.upcloud.base.inventory.vo.EisInvContainerStoreSubVo;
import com.prolog.upcloud.base.inventory.vo.EisInvContainerStoreVo;
import com.prolog.upcloud.base.strategy.domain.core.Strategy;
import com.prolog.upcloud.base.strategy.dto.StrategyDTO;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.OutboundDataSourceDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.OutboundStrategyDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.OutTaskAlgorithmDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.OutTaskDetailAlgorithmDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.WholeOutContainerDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.WholeOutStrategyResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/10/22
 **/
@Service
@Slf4j
public class OutWholeServiceImpl implements OutWholeService {

    @Autowired
    private OutboundWholeDataInitService outboundWholeDataInitService;
    @Autowired
    private DispatchDataGenerateService dispatchDataGenerateService;

    @Override
    public boolean outContainer(WholeStationDto wholeStationDto, WholeOutTaskContainerDto wholeOutTaskContainerDto, StrategyDTO data, OutboundStrategyTargetStationConfig outboundStrategyTargetStationConfig, OutboundStrategyConfigVo outboundStrategyConfigVo) throws Exception {
        boolean success = false;
        List<OutTaskAlgorithmDto> outTaskAlgorithmDtoList = wholeOutTaskContainerDto.getOutTaskAlgorithmDtoList();
        for (OutTaskAlgorithmDto outTaskAlgorithmDto : outTaskAlgorithmDtoList) {
            try {
                OutboundDataSourceDto outboundDataSourceDto = new OutboundDataSourceDto();
                WholeOutContainerDto wholeOutContainerDto = new WholeOutContainerDto();
                wholeOutContainerDto.setInvStockAlgorithmDtoList(wholeOutTaskContainerDto.getInvStockAlgorithmDtoList());
                wholeOutContainerDto.setOutTaskAlgorithmDto(outTaskAlgorithmDto);
                wholeOutContainerDto.setOriginX(outboundStrategyTargetStationConfig.getX());
                wholeOutContainerDto.setOriginY(outboundStrategyTargetStationConfig.getY());
                outboundDataSourceDto.setWholeOutContainerDto(wholeOutContainerDto);
                Map<String, String> strategyType = new HashMap<>();
                strategyType.put("strategyType", "wholeOut");
                OutboundStrategyContext outboundStrategyContext = new OutboundStrategyContext(outboundWholeDataInitService);
                OutboundStrategyDto outboundStrategyDto = new OutboundStrategyDto();
                outboundStrategyDto.setStrategyType(strategyType);
                outboundStrategyDto.setCondition(outboundDataSourceDto);
                outboundStrategyContext.setStrategyData(outboundStrategyDto);
                Strategy strategy = data.createStrategy();
                strategy.execute(outboundStrategyContext);
                OutboundStrategyDto outboundStrategyDto1 = (OutboundStrategyDto) outboundStrategyContext.getStrategyData();
                List<WholeOutStrategyResultDto> result = (List<WholeOutStrategyResultDto>) outboundStrategyDto1.getResult();
                if (!result.isEmpty()) {
                    //生成容器绑定任务
                    //生成搬运任务
                    dispatchDataGenerateService.generateData(result, outTaskAlgorithmDto, wholeStationDto, outboundStrategyConfigVo);
                    success = true;
                    break;
                }
            } catch (Exception ex) {
                log.error("出库调度异常:{}", ex);
                ex.printStackTrace();
            }
        }
        return success;
    }


}
