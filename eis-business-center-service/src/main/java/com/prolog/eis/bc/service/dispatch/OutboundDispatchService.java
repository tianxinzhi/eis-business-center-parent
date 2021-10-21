package com.prolog.eis.bc.service.dispatch;

import com.prolog.eis.bc.facade.vo.OutboundStrategyConfigVo;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/10/21
 **/
public interface OutboundDispatchService {

    void coreDispatch() throws Exception;

    void wholeOutDispatch(OutboundStrategyConfigVo outboundStrategyConfigVo)throws Exception;
}
