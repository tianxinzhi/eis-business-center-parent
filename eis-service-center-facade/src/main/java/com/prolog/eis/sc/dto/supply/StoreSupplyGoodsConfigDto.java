package com.prolog.eis.sc.dto.supply;

import lombok.Data;

import java.util.Date;

/**
 * @author: wuxl
 * @create: 2021-08-18 15:28
 * @Version: V1.0
 */
@Data
public class StoreSupplyGoodsConfigDto {

    private int id;

    /**
     * 汇总id
     */
    private int storeSupplyConfigId;

    /**
     * 商品id
     */
    private int goodsId;

    /**
     * 数量
     */
    private int qty;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 目标区域数量
     */
    private int targetQty;

    /**
     * 源区域数量
     */
    private int sourceQty;
}
