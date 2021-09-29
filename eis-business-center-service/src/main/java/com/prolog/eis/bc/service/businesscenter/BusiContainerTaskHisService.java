package com.prolog.eis.bc.service.businesscenter;

import com.prolog.eis.bc.facade.dto.businesscenter.BusiContainerTaskHisDto;
import com.prolog.framework.core.pojo.Page;

public interface BusiContainerTaskHisService {
    Page<BusiContainerTaskHisDto> getBusiContainerTask(BusiContainerTaskHisDto dto);
}
