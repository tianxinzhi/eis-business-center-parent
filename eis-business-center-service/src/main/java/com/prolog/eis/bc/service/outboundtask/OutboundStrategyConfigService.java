package com.prolog.eis.bc.service.outboundtask;

import com.prolog.eis.bc.facade.vo.OutboundStrategyConfigVo;

/**
 * @Author clarence_she
 * @Date 2021/9/15
 **/
public interface OutboundStrategyConfigService {

    /**
     * 查询出库任务策略
     * @return
     * @throws Exception
     */
    OutboundStrategyConfigVo findConfigByTypeNo(String typeNo)throws Exception;
}
