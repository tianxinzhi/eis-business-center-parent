package com.prolog.eis.bc.service.businesscenter;

import com.prolog.eis.core.model.biz.container.ContainerTaskReportHis;
import com.prolog.eis.bc.facade.dto.businesscenter.ContainerTaskReportHisDto;
import com.prolog.framework.core.pojo.Page;

public interface ContainerTaskReportHisService {
    Page<ContainerTaskReportHis> getContainerTaskReportHisPage(ContainerTaskReportHisDto dto);
}
