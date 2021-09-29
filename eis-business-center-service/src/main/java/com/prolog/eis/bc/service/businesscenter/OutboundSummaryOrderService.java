package com.prolog.eis.bc.service.businesscenter;

import com.prolog.eis.core.model.biz.outbound.OutboundSummaryOrder;
import com.prolog.eis.bc.facade.dto.businesscenter.OutboundSummaryOrderDto;
import com.prolog.framework.core.pojo.Page;

public interface OutboundSummaryOrderService {
    Page<OutboundSummaryOrder> getutboundSummaryOrderPage(OutboundSummaryOrderDto dto);
}

