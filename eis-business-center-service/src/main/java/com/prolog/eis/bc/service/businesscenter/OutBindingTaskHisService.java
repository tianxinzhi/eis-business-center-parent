package com.prolog.eis.bc.service.businesscenter;

import com.prolog.eis.bc.facade.dto.businesscenter.OutBindingTaskHisDto;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskBindDetail;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskBindDetailHis;
import com.prolog.framework.core.pojo.Page;

import java.util.List;

public interface OutBindingTaskHisService {
    Page<OutBindingTaskHisDto> getOutBindingTaskHisPage(OutBindingTaskHisDto dto);

    List<OutboundTaskBindDetailHis> getOutBindingTaskDetail(String id);
}
