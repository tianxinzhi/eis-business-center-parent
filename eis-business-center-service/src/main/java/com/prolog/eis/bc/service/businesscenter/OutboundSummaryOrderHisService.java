package com.prolog.eis.bc.service.businesscenter;

import com.prolog.eis.core.model.biz.outbound.OutboundSummaryOrderHis;
import com.prolog.eis.bc.facade.dto.businesscenter.OutboundSummaryOrderHisDto;
import com.prolog.framework.core.pojo.Page;

public interface OutboundSummaryOrderHisService {
    Page<OutboundSummaryOrderHis> getutboundSummaryOrderHisPage(OutboundSummaryOrderHisDto dto);
}
