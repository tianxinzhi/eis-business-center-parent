package com.prolog.eis.bc.service.dispatch.impl;

import com.prolog.eis.bc.constant.Enterprise;
import com.prolog.eis.bc.constant.OutboundStrategyConfigConstant;
import com.prolog.eis.bc.facade.dto.outbound.WholeOutTaskContainerDto;
import com.prolog.eis.bc.facade.dto.outbound.WholeStationDto;
import com.prolog.eis.bc.facade.vo.OutboundStrategyConfigVo;
import com.prolog.eis.bc.feign.StrategyClient;
import com.prolog.eis.bc.service.dispatch.OutWholeService;
import com.prolog.eis.bc.service.dispatch.OutboundDispatchService;
import com.prolog.eis.bc.service.dispatch.datainit.OutboundWholeDataInitService;
import com.prolog.eis.bc.service.dispatch.strategy.OutboundStrategyContext;
import com.prolog.eis.bc.service.outboundtask.OutboundStrategyConfigService;
import com.prolog.eis.bc.service.outboundtask.OutboundTaskService;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategyConfig;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategyTargetStationConfig;
import com.prolog.framework.common.message.RestMessage;
import com.prolog.upcloud.base.strategy.domain.core.Strategy;
import com.prolog.upcloud.base.strategy.dto.StrategyDTO;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.OutboundStrategyDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.WholeOutContainerDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.WholeOutStrategyResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Describe 出库调度定时器任务
 * @Author clarence_she
 * @Date 2021/10/21
 **/
@Service
@Slf4j
public class OutboundDispatchServiceImpl implements OutboundDispatchService {

    @Autowired
    private OutWholeService outWholeService;
    @Autowired
    private OutboundStrategyConfigService outboundStrategyConfigService;
    @Autowired
    private StrategyClient strategyClient;


    @Override
    public void coreDispatch() throws Exception {
        /**
         * 1.判断开关
         * 2.查询所有配置好的策略
         */
        List<OutboundStrategyConfigVo> outboundStrategyConfigList = outboundStrategyConfigService.findAll();
        for (OutboundStrategyConfigVo outboundStrategyConfig : outboundStrategyConfigList) {
            switch (outboundStrategyConfig.getOutType()) {
                case OutboundStrategyConfigConstant.OUT_TYPE_WHOLE:
                    this.wholeOutDispatch(outboundStrategyConfig);
                    break;
                case OutboundStrategyConfigConstant.OUT_TYPE_COMPOSE_WHOLE:
                    break;
                case OutboundStrategyConfigConstant.OUT_TYPE_BIG_PACKAGE:
                    break;
                case OutboundStrategyConfigConstant.OUT_TYPE_MID_PACKAGE:
                    break;
                case OutboundStrategyConfigConstant.OUT_TYPE_SMALL_PACKAGE:
                    break;
                default:
                    log.error("该配置类型{},出库类型有误", outboundStrategyConfig.getTypeNo());
            }

        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void wholeOutDispatch(OutboundStrategyConfigVo outboundStrategyConfigVo) throws Exception {
        /**
         * 1.查询所有整托任务的站台
         * 2.
         */
        WholeOutTaskContainerDto wholeOutTaskContainerDto = new WholeOutTaskContainerDto();
        List<WholeStationDto> wholeStationDtoList = wholeOutTaskContainerDto.getWholeStationDtoList();
        wholeStationDtoList.sort(Comparator.comparingInt(WholeStationDto::getContainerCount));

        RestMessage<StrategyDTO> strategyDTO = strategyClient.getStrategyDTO(Enterprise.enterpriseId, "EisInStock", Enterprise.categoryOwnId, Enterprise.warehouseId);
        if (!strategyDTO.isSuccess() || strategyDTO.getData() == null) {
            throw new Exception("获取策略失败");
        }
        List<OutboundStrategyTargetStationConfig> outboundStrategyTargetStationConfigList = outboundStrategyConfigVo.getOutboundStrategyTargetStationConfigList();
        StrategyDTO data = strategyDTO.getData();
        for (WholeStationDto wholeStationDto : wholeStationDtoList) {
            for (OutboundStrategyTargetStationConfig outboundStrategyTargetStationConfig : outboundStrategyTargetStationConfigList) {
                if (wholeStationDto.getStationId().equals(outboundStrategyTargetStationConfig.getStationId()) && wholeStationDto.getIsClaim() == 1 && wholeStationDto.getIsLock() == 0) {
                    outWholeService.outContainer(wholeOutTaskContainerDto,data,outboundStrategyTargetStationConfig);
                }
            }
        }

    }
}
