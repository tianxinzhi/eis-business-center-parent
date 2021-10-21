package com.prolog.eis.bc.service.dispatch.impl;

import com.prolog.eis.bc.constant.OutboundStrategyConfigConstant;
import com.prolog.eis.bc.facade.vo.OutboundStrategyConfigVo;
import com.prolog.eis.bc.service.dispatch.OutboundDispatchService;
import com.prolog.eis.bc.service.outboundtask.OutboundStrategyConfigService;
import com.prolog.eis.bc.service.outboundtask.OutboundTaskService;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategyConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Describe 出库调度定时器任务
 * @Author clarence_she
 * @Date 2021/10/21
 **/
@Service
@Slf4j
public class OutboundDispatchServiceImpl implements OutboundDispatchService {

    @Autowired
    private OutboundTaskService outboundTaskService;
    @Autowired
    private OutboundStrategyConfigService outboundStrategyConfigService;

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
    public void wholeOutDispatch(OutboundStrategyConfigVo outboundStrategyConfigVo) throws Exception {

    }
}
