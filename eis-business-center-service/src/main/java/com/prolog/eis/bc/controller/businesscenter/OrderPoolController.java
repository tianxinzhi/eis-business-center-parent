package com.prolog.eis.bc.controller.businesscenter;

import com.prolog.eis.core.model.biz.outbound.OrderPool;
import com.prolog.eis.bc.facade.dto.businesscenter.OrderPoolDto;
import com.prolog.eis.bc.service.businesscenter.OrderPoolService;
import com.prolog.framework.common.message.RestMessage;
import com.prolog.framework.core.pojo.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "实时汇总单")
@RequestMapping("/orderpool")
public class OrderPoolController {
    @Autowired
    private OrderPoolService orderPoolService;

    @ApiOperation(value = "实时汇总单查询", notes = "实时汇总单查询")
    @PostMapping("/getorderpool")
    @ApiImplicitParams({@ApiImplicitParam(name = "OutBindingTaskDto", value = "实时汇总单")})
    public RestMessage<Page<OrderPool>> getOrderPoolPage (@RequestBody OrderPoolDto dto){
        Page<OrderPool> page = orderPoolService.getOrderPoolPage(dto);
        return RestMessage.newInstance(true,"成功",page);
    }

    @ApiOperation(value = "实时汇总单新增", notes = "实时汇总单新增")
    @PostMapping("/add-orderpool")
    @ApiImplicitParams({@ApiImplicitParam(name = "OrderPool", value = "实时汇总单")})
    public RestMessage<OrderPool> add(@RequestBody OrderPool orderPool){
        if(orderPoolService.add(orderPool) > 0){
            return RestMessage.newInstance(true,"新增成功", null);
        }
        else
        {
            return RestMessage.newInstance(true,"未新增数据", null);
        }


    }


    @ApiOperation(value = "实时汇总单修改", notes = "实时汇总单修改")
    @PostMapping("/modify-orderpool")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "主键id", required = false)})
    public RestMessage<OrderPool> Modify(@RequestBody OrderPool orderPool){
        if(orderPoolService.modify(orderPool) == 0){
            return RestMessage.newInstance(true,"ID数据不存在", null);
        }
        else{
            return RestMessage.newInstance(true,"成功", null);
        }

    }

    @ApiOperation(value = "实时汇总单删除", notes = "实时汇总单删除")
    @GetMapping("/deleted-orderpool")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "主键id", required = false)})
    public RestMessage<OrderPool> deleted(@RequestParam(value = "id", required = true) String id){
        orderPoolService.deleted(id);
        return RestMessage.newInstance(true,"成功", null);
    }
}
