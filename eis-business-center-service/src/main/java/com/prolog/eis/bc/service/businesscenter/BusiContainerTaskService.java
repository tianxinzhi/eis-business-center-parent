package com.prolog.eis.bc.service.businesscenter;

import com.prolog.eis.bc.facade.dto.businesscenter.BusiContainerTaskDto;
import com.prolog.framework.core.pojo.Page;

public interface BusiContainerTaskService {
    Page<BusiContainerTaskDto> getBusiContainerTask(BusiContainerTaskDto dto);
    void updatePriority(String id,int priority);
}
