package com.prolog.eis.bc.service.businesscenter;

import com.prolog.eis.bc.facade.dto.businesscenter.OutboundTaskDto;
import com.prolog.framework.core.pojo.Page;

public interface OutTaskManagerService {
    Page<OutboundTaskDto>  getOuttaskPage(OutboundTaskDto dto);
    void updatePriority(String id,int priority);
}
