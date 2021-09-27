package com.prolog.eis.bc.service.osr;

import com.prolog.eis.bc.facade.dto.osr.OutSummaryOrderInfoDto;

/**
 * @Author: txz
 * @Date: 2021/9/23 15:47
 */
public interface OutboundSummaryOrderService {

    /**
     * 根据外部订单生成出库汇总单
     * @param dto
     * @return
     */
    String createOutOrder(OutSummaryOrderInfoDto dto) throws Exception;
}
