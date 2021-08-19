package com.prolog.eis.sc.dto.supply;

import com.prolog.eis.model.route.supply.StoreAreaLocationDetail;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: wuxl
 * @create: 2021-08-18 15:30
 * @Version: V1.0
 */
@Data
public class StoreAreaLocationDto {

    /**
     * id
     */
    private int id;

    /**
     * 库存区域
     */
    private String storeArea;

    /**
     * 是否开启（0否 1是）
     */
    private int isAvailable;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 商品配置集合
     */
    private List<StoreAreaLocationDetail> locationDetailList;
}
