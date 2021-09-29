package com.prolog.eis.bc.service.businesscenter;

import com.prolog.eis.bc.facade.dto.businesscenter.OutBindingTaskDto;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskBindDetail;
import com.prolog.framework.core.pojo.Page;

import java.util.List;

public interface OutBindingTaskService {

    Page<OutBindingTaskDto> getOuttaskHisPage(OutBindingTaskDto dto);

    List<OutboundTaskBindDetail> getOutBindingTaskDetail(String id);
//    void updatePriority(String id,int priority);


}
