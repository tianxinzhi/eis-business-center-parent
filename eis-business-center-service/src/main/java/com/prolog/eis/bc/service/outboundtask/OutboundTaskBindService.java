package com.prolog.eis.bc.service.outboundtask;

import java.util.List;

import com.prolog.eis.component.algorithm.composeorder.entity.ContainerDto;

/**
 * 出单任务绑定Service
 * @author 金总
 *
 */
public interface OutboundTaskBindService {

    /**
     * 根据拣选单Id集合查询关联的容器信息
     * @param pickingOrderIdList 拣选单Id集合
     * @param storeMatchingStrategy 匹配策略
     * @return
     */
    List<ContainerDto> findByPickingOrderIdList(List<String> pickingOrderIdList,
            int storeMatchingStrategy);
}
