package com.prolog.eis.sc.dto.supply;

import com.prolog.eis.model.route.supply.StoreSupplyGoodsConfig;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: wuxl
 * @create: 2021-08-18 15:28
 * @Version: V1.0
 */
@Data
public class StoreSupplyConfigDto {

    /**
     * id
     */
    private int id;

    /**
     * 起始区域
     */
    private String sourceArea;

    /**
     * 目标区域
     */
    private String targetArea;

    /**
     * 是否开启（0否 1是）
     */
    private int isAvailable;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 关系明细配置集合
     */
    private List<StoreSupplyGoodsConfig> goodsConfigList;
}
