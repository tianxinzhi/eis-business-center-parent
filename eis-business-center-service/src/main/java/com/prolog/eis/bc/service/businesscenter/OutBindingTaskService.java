package com.prolog.eis.bc.service.businesscenter;

import com.prolog.eis.bc.facade.dto.businesscenter.OutBindingTaskDto;
import com.prolog.framework.core.pojo.Page;

public interface OutBindingTaskService {

    Page<OutBindingTaskDto> getOuttaskHisPage(OutBindingTaskDto dto);
//    void updatePriority(String id,int priority);


}
