package com.prolog.eis.bc.feign.product;

import com.prolog.eis.bc.facade.dto.product.GoodsInventoryInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: txz
 * @Date: 2021/9/14 13:56
 * @Desc: 调用远程商品服务
 */
@FeignClient(value = "service-ai-eis-route-dispatch")
//TODO:确定以下api路径
public interface GoodsInfoInterfaceFeign {

    /**
     * 通过商品ID查商品服务（地址暂空），获取业主、商品名称、商品条码信息
     * @param id
     * @return GoodsInventoryInfoDto
     */
    @RequestMapping(value = "/getGoodsInfo",method = RequestMethod.POST)
    GoodsInventoryInfoDto getGoodsInfo(String id);

    /**
     * 通过批号ID查询库存服务，获取商品批次信息
     * @param batchId
     * @return getGoodsBatchInfo
     */
    @RequestMapping(value = "/getGoodsBatchInfo",method = RequestMethod.POST)
    GoodsInventoryInfoDto getGoodsBatchInfo(String batchId);

    /**
     * 通过批号ID+商品ID查询库存服务，获取库存数量
     * @param batchId
     * @param goodsId
     * @return Integer
     */
    @RequestMapping(value = "/getGoodsStore",method = RequestMethod.POST)
    Integer getGoodsStore(@RequestParam("batchId")String batchId, @RequestParam("goodsId")String goodsId);

    /**
     * 通过点位查询仓库服务，获取该货位是否锁定，统计该商品+批次的所有容器一共有多少锁定数量=锁定数量
     * @param sourceLoc
     * @param batchId
     * @param goodsId
     * @return Integer
     */
    @RequestMapping(value = "/getGoodsLockInfo",method = RequestMethod.POST)
    Integer getGoodsLockInfo(@RequestParam("sourceLoc") String sourceLoc, @RequestParam("batchId")String batchId, @RequestParam("goodsId")String goodsId);
}
