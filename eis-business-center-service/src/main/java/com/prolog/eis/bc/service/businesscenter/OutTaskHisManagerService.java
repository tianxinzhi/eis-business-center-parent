package com.prolog.eis.bc.service.businesscenter;

import com.prolog.eis.bc.facade.dto.businesscenter.OutboundTaskHisDto;
import com.prolog.framework.core.pojo.Page;

public interface OutTaskHisManagerService {

    Page<OutboundTaskHisDto> getOuttaskHisPage(OutboundTaskHisDto dto);
}
