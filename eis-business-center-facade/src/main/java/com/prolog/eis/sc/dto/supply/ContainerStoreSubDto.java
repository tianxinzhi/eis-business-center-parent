package com.prolog.eis.sc.dto.supply;

import lombok.Data;

import java.util.List;

/**
 * @author: wuxl
 * @create: 2021-08-19 10:24
 * @Version: V1.0
 */
@Data
public class ContainerStoreSubDto {


    private int id;
    /**
     * 母容器库存ID
     */
    private int containerStoreId;

    /**
     * 子容器号
     */
    private String containerStoreSubNo;

    /**
     * 业主ID
     */
    private int ownerId;

    /**
     * 商品ID
     */
    private int goodsId;

    /**
     * 批次ID
     */
    private int lotId;

    /**
     * 数量
     */
    private double qty;

    /**
     * 托盘集合
     */
    private List<ContainerStoreDto> storeDtoList;
}
