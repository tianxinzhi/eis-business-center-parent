package com.prolog.eis.bc.service.product.impl;

import com.prolog.eis.bc.dao.product.GoodsInventoryWarnMapper;
import com.prolog.eis.bc.facade.dto.product.GoodsInventoryInfoDto;
import com.prolog.eis.bc.feign.product.GoodsBatchNumFeign;
import com.prolog.eis.bc.feign.product.GoodsInfoInterfaceFeign;
import com.prolog.eis.bc.service.product.GoodsInventoryWarnService;
import com.prolog.framework.bz.common.search.SearchApi;
import com.prolog.framework.core.pojo.Page;
import com.prolog.framework.dao.util.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;

/**
 * @Author: xiaozhi
 * @Date: 2021/9/14 11:41
 * @Desc:
 */
@Service
public class GoodsInventoryWarnServiceImpl implements GoodsInventoryWarnService {

    @Autowired
    private GoodsInventoryWarnMapper mapper;
    @Autowired
    private GoodsInfoInterfaceFeign client;
    @Autowired
    private GoodsBatchNumFeign batchNumClient;
    @Autowired
    private SearchApi searchApi;

    @Override
    public Page<GoodsInventoryInfoDto> page(GoodsInventoryInfoDto dto) throws Exception{
        if( StringUtils.isEmpty(dto.getPageSize()) || StringUtils.isEmpty(dto.getPageNum())) {
            throw new RuntimeException("请传入对应的分页参数");
        }

//        List<Double> lockNum = searchApi.search("getLockNumByLocation"
//                , MapUtils.put("locationNo",dto.getSourceLocation()).getMap()
//                , Double.class);

        List<GoodsInventoryInfoDto> list2 = searchApi.search("getGoodsInventoryWarnInfo", new HashMap<>(),GoodsInventoryInfoDto.class);


//        //查询list返回结果
//        List<GoodsInventoryInfoDto> list = mapper.page(dto).stream().distinct().map(good ->{
//            try {
//
//                GoodsInventoryInfoDto goodsInfo = client.getGoodsInfo(good.getItemId()).getData();//获取商品
//                GoodsInventoryInfoDto goodsBatchInfo = batchNumClient.getGoodsBatchInfo((String[]) Arrays.asList(good.getLotId()).toArray()).getData().get(0);//获取商品批次
//                Double goodsStore = batchNumClient.getGoodsStore(good.getLotId(), good.getItemId()).getData();//获取库存
//
//                Double lockNum = searchApi.search("getLockNumByLocation"
//                        , MapUtils.put("locationNo",dto.getSourceLocation()).getMap()
//                        , Double.class).get(0);
//
//                good.setOwnerName(goodsInfo.getOwnerName());
//                good.setItemName(goodsInfo.getItemName());
//                good.setItemCode(goodsInfo.getItemCode());
//                good.setBatchNum(goodsBatchInfo.getBatchNum());
//                good.setStoreQty(goodsStore);
//                good.setLockQty(lockNum);
//                good.setStockoutQty(good.getOrderQty() - goodsStore);
//                good.setLockStockoutQty(goodsStore - lockNum - good.getOrderQty());
//                //计算
//                // 锁定缺货数量=库存数量-锁定数量-订单数量
//                // 只显示有缺货的数据，即缺货数量or锁定缺货数量<0的数据行
//                // 按缺货数量、锁定缺货数量倒序排列，查询TOP500，分页
//
//            } catch (JsonProcessingException | IllegalAccessException |InstantiationException e) {
//                e.printStackTrace();
//            }
//            return good;
//        }).sorted(Comparator.comparing(GoodsInventoryInfoDto::getStockoutQty).reversed())
//           .sorted(Comparator.comparing(GoodsInventoryInfoDto::getLockStockoutQty).reversed())
//           .collect(Collectors.toList());

        return PageUtils.getPage(list2);
    }
}
