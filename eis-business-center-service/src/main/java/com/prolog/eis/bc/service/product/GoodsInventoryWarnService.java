package com.prolog.eis.bc.service.product;

import com.prolog.eis.bc.facade.dto.product.GoodsInventoryInfoDto;
import com.prolog.framework.core.pojo.Page;

/**
 * @Author: xiaozhi
 * @Date: 2021/9/14 11:18
 * @Desc:
 */
public interface GoodsInventoryWarnService {

    /**
     * 获取商品库存信息
     * @param dto
     * @return
     */
    Page<GoodsInventoryInfoDto> page(GoodsInventoryInfoDto dto) throws Exception;
}
