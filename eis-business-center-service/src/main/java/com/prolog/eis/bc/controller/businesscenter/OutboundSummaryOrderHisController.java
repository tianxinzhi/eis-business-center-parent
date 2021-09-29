package com.prolog.eis.bc.controller.businesscenter;

import com.prolog.eis.bc.facade.dto.businesscenter.OutboundSummaryOrderHisDto;
import com.prolog.eis.core.model.biz.outbound.OutboundSummaryOrderHis;
import com.prolog.eis.bc.service.businesscenter.OutboundSummaryOrderHisService;
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

@RestController
@Api(tags = "出库汇总任务单历史管理")
@RequestMapping("/outtasksummaryorder-his")
public class OutboundSummaryOrderHisController {
    @Autowired
    private OutboundSummaryOrderHisService service;

    @ApiOperation(value = "出库汇总任务单历史查询", notes = "出库汇总任务单历史查询")
    @PostMapping("/getouttasksummaryorder-his")
    @ApiImplicitParams({@ApiImplicitParam(name = "OutboundSummaryOrderDto", value = "出库任务单回告")})
    public RestMessage<Page<OutboundSummaryOrderHis>> getOutboundTaskReportPage (@RequestBody OutboundSummaryOrderHisDto dto){
        Page<OutboundSummaryOrderHis> page = service.getutboundSummaryOrderHisPage(dto);
        return RestMessage.newInstance(true,"成功",page);
    }
}
