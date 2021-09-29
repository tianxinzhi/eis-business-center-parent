package com.prolog.eis.bc.feign.product;

import com.prolog.eis.bc.facade.dto.product.GoodsInventoryInfoDto;
import com.prolog.framework.common.message.RestMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: txz
 * @Date: 2021/9/14 13:56
 * @Desc: 调用远程商品服务
 */
@FeignClient(value = "upcloud-base-item-dlt")
public interface GoodsInfoInterfaceFeign {

    /**
     * 通过商品ID查商品服务（地址暂空），获取业主、商品名称、商品条码信息
     * @param id
     * @return GoodsInventoryInfoDto
     */
    @GetMapping(value = "/item")
    RestMessage<GoodsInventoryInfoDto> getGoodsInfo(String id);

 }
