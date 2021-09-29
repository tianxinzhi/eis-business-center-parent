package com.prolog.eis.bc.controller.businesscenter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prolog.eis.bc.facade.dto.businesscenter.OutboundSummaryOrderDto;
import com.prolog.eis.bc.service.osr.OutboundSummaryOrderService;
import com.prolog.eis.core.model.biz.outbound.OutboundSummaryOrder;
import com.prolog.framework.common.message.RestMessage;
import com.prolog.framework.core.pojo.Page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "出库汇总任务单管理")
@RequestMapping("/outtasksummaryorder")
public class OutboundSummaryOrderController {
    @Autowired
    private OutboundSummaryOrderService service;

    @ApiOperation(value = "出库汇总任务单查询", notes = "出库汇总任务单查询")
    @PostMapping("/getouttasksummaryorder")
    @ApiImplicitParams({@ApiImplicitParam(name = "OutboundSummaryOrderDto", value = "出库任务单回告")})
    public RestMessage<Page<OutboundSummaryOrder>> getOutboundTaskReportPage (@RequestBody OutboundSummaryOrderDto dto){
        Page<OutboundSummaryOrder> page = service.getutboundSummaryOrderPage(dto);
        return RestMessage.newInstance(true,"成功",page);
    }
}
