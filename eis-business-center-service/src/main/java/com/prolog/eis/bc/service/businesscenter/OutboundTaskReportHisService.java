package com.prolog.eis.bc.service.businesscenter;

import com.prolog.eis.core.model.biz.outbound.OutboundTaskReportHis;
import com.prolog.eis.bc.facade.dto.businesscenter.OutboundTaskReportHisDto;
import com.prolog.framework.core.pojo.Page;

public interface OutboundTaskReportHisService {
    Page<OutboundTaskReportHis> getOutboundTaskReportPage(OutboundTaskReportHisDto dto);
}
