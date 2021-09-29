package com.prolog.eis.bc.service.businesscenter;

import com.prolog.eis.bc.facade.dto.businesscenter.OutBindingTaskHisDto;
import com.prolog.framework.core.pojo.Page;

public interface OutBindingTaskHisService {
    Page<OutBindingTaskHisDto> getOutBindingTaskHisPage(OutBindingTaskHisDto dto);
}
