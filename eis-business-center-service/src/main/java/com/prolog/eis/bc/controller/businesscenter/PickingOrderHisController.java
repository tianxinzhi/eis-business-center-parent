package com.prolog.eis.bc.controller.businesscenter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prolog.eis.bc.facade.dto.businesscenter.PickingOrderDto2;
import com.prolog.eis.bc.service.businesscenter.PickingOrderHisService;
import com.prolog.eis.core.model.biz.outbound.PickingOrderHis;
import com.prolog.framework.common.message.RestMessage;
import com.prolog.framework.core.pojo.Page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "拣选单历史管理")
@RequestMapping("/pickingorder-his")
public class PickingOrderHisController {
    @Autowired
    private PickingOrderHisService service;

    @ApiOperation(value = "拣选单管理历史查询", notes = "拣选单管理历史查询")
    @PostMapping("/getpickingorder-his")
    @ApiImplicitParams({@ApiImplicitParam(name = "PickingOrderDto", value = "拣选单")})
    public RestMessage<Page<PickingOrderHis>> getOuttaskPage (@RequestBody PickingOrderDto2 dto){
        Page<PickingOrderHis> page = service.getPickingOrderHisPage(dto);
        return RestMessage.newInstance(true,"成功",page);
    }
}
