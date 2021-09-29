package com.prolog.eis.bc.feign.product;

import com.prolog.eis.bc.facade.dto.product.GoodsInventoryInfoDto;
import com.prolog.framework.common.message.RestMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: txz
 * @Date: 2021/9/27 13:56
 * @Desc: 调用远程商品批次服务
 */
@FeignClient(value = "upcloud-base-inventory")
public interface GoodsBatchNumFeign {

    /**
     * 通过批号ID查询库存服务，获取商品批次信息
     * @param batchId
     * @return getGoodsBatchInfo
     */
    @RequestMapping(value = "batch-number/v1/getBatchInfos",method = RequestMethod.POST)
    RestMessage<List<GoodsInventoryInfoDto>> getGoodsBatchInfo(String[] batchId);


    /**
     * 通过批号ID+商品ID查询库存服务，获取库存数量
     * @param batchId
     * @param goodsId
     * @return Integer
     */
    @RequestMapping(value = "/api/v1/container-store/getStoreByItemAndBacth",method = RequestMethod.POST)
    RestMessage<Double> getGoodsStore(@RequestParam("batchId") String batchId, @RequestParam("goodsId") String goodsId);

  }
