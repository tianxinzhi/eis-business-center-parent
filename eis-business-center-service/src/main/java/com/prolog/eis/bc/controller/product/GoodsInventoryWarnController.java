package com.prolog.eis.bc.controller.product;


import com.prolog.eis.bc.facade.dto.product.GoodsInventoryInfoDto;
import com.prolog.eis.bc.facade.dto.product.GoodsInventoryWarnDefineDto;
import com.prolog.eis.bc.service.product.GoodsInventoryWarnService;
import com.prolog.framework.common.message.RestMessage;
import com.prolog.framework.core.pojo.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author txz
 * @date 2021/09/14
 */
@RestController
@Api(tags = "库存预警")
@RequestMapping("goodsinventorywarn")
public class GoodsInventoryWarnController {

    @Autowired
    private GoodsInventoryWarnService goodsInventoryWarnService;

    @ApiOperation(value = "库存预警查询", notes = "库存预警查询")
    @PostMapping("/get-Goods-Inventory")
    @ApiImplicitParams({@ApiImplicitParam(name = "GoodsInventoryWarnDefineDto", value = "分页", required = false)})
    public RestMessage<Page<GoodsInventoryInfoDto>> page(@RequestBody GoodsInventoryWarnDefineDto dto){
        Page<GoodsInventoryInfoDto> page = goodsInventoryWarnService.page(dto);
        return RestMessage.newInstance(true,"成功",page);

    }

}
