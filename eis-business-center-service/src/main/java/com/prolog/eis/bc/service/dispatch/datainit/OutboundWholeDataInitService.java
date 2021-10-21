package com.prolog.eis.bc.service.dispatch.datainit;

import com.prolog.eis.bc.facade.vo.OutboundStrategyConfigVo;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.WholeOutContainerDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.WholeOutTaskContainerDto;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/10/21
 **/
public interface OutboundWholeDataInitService {

    /**
     * 查询整托出库数据初始化
     * @return
     */
    WholeOutContainerDto findWholeOutData();

    /**
     * 查询整托出库数据初始化-临时
     * @return
     */
    WholeOutTaskContainerDto findWholeOutData(OutboundStrategyConfigVo config);
}
