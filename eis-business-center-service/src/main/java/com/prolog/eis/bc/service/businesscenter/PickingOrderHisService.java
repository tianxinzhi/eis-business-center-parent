package com.prolog.eis.bc.service.businesscenter;

import com.prolog.eis.bc.facade.dto.businesscenter.PickingOrderDto2;
import com.prolog.eis.core.model.biz.outbound.PickingOrderHis;
import com.prolog.framework.core.pojo.Page;

public interface PickingOrderHisService {
    Page<PickingOrderHis> getPickingOrderHisPage(PickingOrderDto2 dto);
}
