package com.prolog.eis.bc.service.dispatch;

import com.prolog.eis.bc.facade.vo.OutboundStrategyConfigVo;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/10/21
 **/
public interface OutboundDispatchService {

    /**
     * 出库调度
     * @throws Exception
     */
    void coreDispatch() throws Exception;

    /**
     * 整托出库
     * @param outboundStrategyConfigVo
     * @throws Exception
     */
    void wholeOutDispatch(OutboundStrategyConfigVo outboundStrategyConfigVo)throws Exception;
}
