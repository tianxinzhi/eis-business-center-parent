package com.prolog.eis.bc.service.businesscenter;

import com.prolog.eis.bc.facade.dto.businesscenter.ContainerTaskReportDto;
import com.prolog.eis.core.model.biz.container.ContainerTaskReport;
import com.prolog.framework.core.pojo.Page;

public interface ContainerTaskReportService {
    Page<ContainerTaskReport> getContainerTaskReportPage(ContainerTaskReportDto dto);
}
