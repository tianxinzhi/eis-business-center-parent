package com.prolog.eis.bc.controller.businesscenter;

import com.prolog.eis.bc.facade.vo.OutboundTaskBindVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.prolog.eis.bc.facade.dto.businesscenter.PickingOrderDto2;
import com.prolog.eis.bc.service.pickingorder.PickingOrderService;
import com.prolog.eis.core.model.biz.outbound.PickingOrder;
import com.prolog.framework.common.message.RestMessage;
import com.prolog.framework.core.pojo.Page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.List;

@RestController
@Api(tags = "拣选单管理")
@RequestMapping("/pickingorder")
public class PickingOrderController {
    @Autowired
    private PickingOrderService service;

    @ApiOperation(value = "拣选单管理查询", notes = "拣选单管理查询")
    @PostMapping("/getpickingorder")
    @ApiImplicitParams({@ApiImplicitParam(name = "PickingOrderDto", value = "拣选单")})
    public RestMessage<Page<PickingOrder>> getpickingorder (@RequestBody PickingOrderDto2 dto){
        Page<PickingOrder> page = service.getPickingOrderPage(dto);
        return RestMessage.newInstance(true,"成功",page);
    }

    @ApiOperation(value = "通过拣选单Id查询容器绑定任务", notes = "通过拣选单Id查询容器绑定任务")
    @GetMapping("/getContainerByPickingOrderId")
    public RestMessage<List<OutboundTaskBindVo>> getContainerByPickingOrderId (@RequestParam String id){
        List<OutboundTaskBindVo> outboundTaskBindVoList = service.getContainerByPickingOrderId(id);
        return RestMessage.newInstance(true,"成功",outboundTaskBindVoList);
    }

}
