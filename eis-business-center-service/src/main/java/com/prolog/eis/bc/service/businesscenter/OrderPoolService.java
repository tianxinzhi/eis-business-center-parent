package com.prolog.eis.bc.service.businesscenter;

import com.prolog.eis.core.model.biz.outbound.OrderPool;
import com.prolog.eis.bc.facade.dto.businesscenter.OrderPoolDto;
import com.prolog.framework.core.pojo.Page;

public interface OrderPoolService {
    Page<OrderPool> getOrderPoolPage(OrderPoolDto dto);
    long add(OrderPool orderPool);
    long modify(OrderPool orderPool);
    void deleted(String id);
}
