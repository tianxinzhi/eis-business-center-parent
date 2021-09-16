package com.prolog.eis.bc.service.product.impl;

import com.prolog.eis.bc.dao.product.GoodsInventoryWarnMapper;
import com.prolog.eis.bc.facade.dto.product.GoodsInventoryInfoDto;
import com.prolog.eis.bc.facade.dto.product.GoodsInventoryWarnDefineDto;
import com.prolog.eis.bc.feign.product.GoodsInfoInterfaceFeign;
import com.prolog.eis.bc.service.product.GoodsInventoryWarnService;
import com.prolog.framework.core.pojo.Page;
import com.prolog.framework.dao.util.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public Page<GoodsInventoryInfoDto> page(GoodsInventoryWarnDefineDto dto) {
        if( StringUtils.isEmpty(dto.getPageSize()) || StringUtils.isEmpty(dto.getPageNum())) {
            throw new RuntimeException("请传入对应的分页参数");
        }
        //查询list返回结果
        List<GoodsInventoryInfoDto> list = mapper.page(dto).stream().distinct().map(good ->{
            /**GoodsInventoryInfoDto goodsInfo = client.getGoodsInfo(good.getItemId());//获取商品
            GoodsInventoryInfoDto goodsBatchInfo = client.getGoodsBatchInfo(good.getLotId());//获取商品批次
            Integer goodsStore = client.getGoodsStore(good.getLotId(), good.getItemId());//获取库存
            Integer goodsLockInfo = client.getGoodsLockInfo(good.getSourceLocation(), good.getLotId(), good.getItemId());//获取锁定数量
            */
             //计算
            // 锁定缺货数量=库存数量-锁定数量-订单数量
            // 只显示有缺货的数据，即缺货数量or锁定缺货数量<0的数据行
            // 按缺货数量、锁定缺货数量倒序排列，查询TOP500，分页
            return good;
        }).collect(Collectors.toList());

        return PageUtils.getPage(list);
    }
}
