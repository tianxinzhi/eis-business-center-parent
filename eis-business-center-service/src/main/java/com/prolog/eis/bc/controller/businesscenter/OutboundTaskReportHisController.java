package com.prolog.eis.bc.controller.businesscenter;

import com.prolog.eis.core.model.biz.outbound.OutboundTaskReportHis;
import com.prolog.eis.bc.facade.dto.businesscenter.OutboundTaskReportHisDto;
import com.prolog.eis.bc.service.businesscenter.OutboundTaskReportHisService;
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
@Api(tags = "出库任务单回告历史管理")
@RequestMapping("/outtaskreporthis")
public class OutboundTaskReportHisController {
    @Autowired
    private OutboundTaskReportHisService service;

    @ApiOperation(value = "出库任务单回告历史查询", notes = "出库任务单回告历史查询")
    @PostMapping("/getouttaskreporthis")
    @ApiImplicitParams({@ApiImplicitParam(name = "OutboundTaskReportHisDto", value = "出库任务单回告历史")})
    public RestMessage<Page<OutboundTaskReportHis>> getOutboundTaskReportHisPage (@RequestBody OutboundTaskReportHisDto dto){
        Page<OutboundTaskReportHis> page = service.getOutboundTaskReportPage(dto);
        return RestMessage.newInstance(true,"成功",page);
    }
}
