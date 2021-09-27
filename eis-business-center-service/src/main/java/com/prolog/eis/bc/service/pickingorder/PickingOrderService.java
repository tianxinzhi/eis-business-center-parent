package com.prolog.eis.bc.service.pickingorder;

import java.util.List;

import com.prolog.eis.component.algorithm.composeorder.entity.PickingOrderDto;

/**
 * 拣选单服务层
 */
public interface PickingOrderService {

    /**
     * 生成拣选单
     * @return 拣选单Id
     */
    String insert(String stationId) throws Exception;

    /**
     * 根据站台Id查询关联拣选单(包含其他业务对象)
     * @param stationId 站台Id
     * @param storeMatchingStrategy 存储匹配策略
     * @return 关联拣选单
     */
    List<PickingOrderDto> findByStationId(String stationId,
            int storeMatchingStrategy);
}
