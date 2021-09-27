package com.prolog.eis.bc.service.outboundtask;

import java.util.Map;

/**
 * 出单任务绑定详情Service
 * @author 金总
 *
 */
public interface OutboundTaskBindDtService {

    /**
     * 根据批次Id分组查询绑定数量总和
     * @return Map,key=批次Id,value=绑定数量和
     */
    Map<String, Integer> findSumBindingNumGroupByLotId();

    /**
     * 根据商品Id分组查询绑定数量总和
     * @return Map,key=商品Id,value=绑定数量和
     */
    Map<String, Integer> findSumBindingNumGroupByItemId();
}
