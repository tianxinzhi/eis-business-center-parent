package com.prolog.eis.bc.service.outboundtask.impl;

import com.prolog.eis.bc.constant.OutboundStrategyConfigConstant;
import com.prolog.eis.bc.facade.vo.OutboundStrategyConfigVo;
import com.prolog.eis.bc.service.outboundtask.ContainerOutDispatchService;
import com.prolog.eis.bc.service.outboundtask.OutboundStrategyConfigService;
import com.prolog.eis.bc.service.outboundtask.OutboundTaskService;
import com.prolog.eis.component.algorithm.composeorder.ComposeOrderUtils;
import com.prolog.eis.component.algorithm.composeorder.entity.BizOutTask;
import com.prolog.eis.component.algorithm.composeorder.entity.StationDto;
import com.prolog.eis.component.algorithm.composeorder.entity.WarehouseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/9/13
 **/
@Service
public class OutboundTaskServiceImpl implements OutboundTaskService {

    private final static Logger logger = LoggerFactory.getLogger(OutboundTaskServiceImpl.class);

    @Autowired
    private ContainerOutDispatchService containerOutDispatchService;
    @Autowired
    private OutboundStrategyConfigService outboundStrategyConfigService;

    @Override
    public void composeAndGenerateOutbound() {
        /**
         * 1.数据初始化
         *      调用出库任务策略，查询任务类型
         * 2.调用组单算法
         *      根据出库任务策略，进行组单的算法,并生成拣选单任务
         * 3.调用出库调度
         *      根据出库任务策略，找托盘，并生成容器绑定任务以及生成容器搬运任务
         */
        try {
            OutboundStrategyConfigVo config = outboundStrategyConfigService.findConfigByTypeNo(OutboundStrategyConfigConstant.TYPE_B2C);
            String outModel = config.getOutModel();
            if(outModel.equals(OutboundStrategyConfigConstant.OUT_MODEL_PICKING)){
                String composeOrderConfig = config.getComposeOrderConfig();
                List<BizOutTask> outTaskList = null;
                StationDto station = null;
                WarehouseDto warehouse = null;
                if(composeOrderConfig.equals(OutboundStrategyConfigConstant.ALGORITHM_COMPOSE_SIMILARITY)) {
                    List<BizOutTask> bestBizOutTask = ComposeOrderUtils.compose(station, warehouse, outTaskList);

                }
            }else{

            }
        } catch (Exception ex) {
            logger.error("组单调度异常 {}", ex.toString());
        }
    }
}
