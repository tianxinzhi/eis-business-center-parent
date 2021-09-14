package com.prolog.eis.sc.service.supply;

/**
 * @author: wuxl
 * @create: 2021-09-14 15:22
 * @Version: V1.0
 */
public interface ScStoreSupplyService {

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
