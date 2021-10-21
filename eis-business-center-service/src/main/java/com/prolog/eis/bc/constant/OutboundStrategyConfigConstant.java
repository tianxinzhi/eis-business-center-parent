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

    // 出库策略->1.整托出库、2.大包装拼托出库、3.大包装出库、4.中包装出库、5.小包装出库
    public static final int OUT_TYPE_WHOLE = 1;
    public static final int OUT_TYPE_COMPOSE_WHOLE = 2;
    public static final int OUT_TYPE_BIG_PACKAGE = 3;
    public static final int OUT_TYPE_MID_PACKAGE = 4;
    public static final int OUT_TYPE_SMALL_PACKAGE = 5;
}
