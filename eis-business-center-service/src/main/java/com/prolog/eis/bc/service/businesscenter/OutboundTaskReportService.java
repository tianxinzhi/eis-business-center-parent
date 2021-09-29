package com.prolog.eis.bc.service.businesscenter;

import com.prolog.eis.core.model.biz.outbound.OutboundTaskReport;
import com.prolog.eis.bc.facade.dto.businesscenter.OutboundTaskReportDto;
import com.prolog.framework.core.pojo.Page;

public interface OutboundTaskReportService {
    Page<OutboundTaskReport> getOutboundTaskReportPage(OutboundTaskReportDto dto);
}
