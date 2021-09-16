package com.prolog.eis.bc.service.supply;

/**
 * @author: wuxl
 * @create: 2021-09-14 15:22
 * @Version: V1.0
 */
public interface SupplyDispatchService {

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
