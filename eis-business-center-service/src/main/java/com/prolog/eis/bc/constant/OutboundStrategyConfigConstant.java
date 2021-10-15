package com.prolog.eis.bc.constant;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/9/15
 **/
public interface OutboundStrategyConfigConstant {
    public static final String TYPE_B2C = "B2C";

    public static final String OUT_MODEL_PICKING = "PICKING_ORDER_OUT";
    public static final String OUT_MODEL_ORDER_POOL = "ORDER_POOL_OUT";

    public static final String ALGORITHM_COMPOSE_SIMILARITY = "SIMILARITY";

    public static final int STORE_MATCHING_STRATEGY_ITEM = 1;
    public static final int STORE_MATCHING_STRATEGY_IOT = 2;
}
