package com.prolog.eis.sc.service.supply;

/**
 * @author: wuxl
 * @create: 2021-08-18 14:58
 * @Version: V1.0
 */
public interface StoreSupplyService {

    /**
     * 安全补货调度
     *
     * @throws Exception
     */
    void safeSupplyDispatch() throws Exception;

    /**
     * 紧急补货调度
     *
     * @throws Exception
     */
    void urgentSupplyDispatch() throws Exception;

}
