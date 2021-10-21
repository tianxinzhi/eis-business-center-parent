package com.prolog.eis.bc.controller.businesscenter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prolog.eis.bc.facade.dto.businesscenter.OutboundTaskReportDto;
import com.prolog.eis.bc.service.businesscenter.OutboundTaskReportService;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskReport;
import com.prolog.framework.common.message.RestMessage;
import com.prolog.framework.core.pojo.Page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "出库任务单回告管理")
@RequestMapping("/outtaskreport")
public class OutboundTaskReportController {
    @Autowired
    private OutboundTaskReportService service;

    @ApiOperation(value = "出库任务单回告查询", notes = "出库任务单回告查询")
    @PostMapping("/getouttaskreport")
    @ApiImplicitParams({@ApiImplicitParam(name = "OutboundTaskReportDto", value = "出库任务单回告")})
    public RestMessage<Page<OutboundTaskReport>> getOutboundTaskReportPage (@RequestBody OutboundTaskReportDto dto){
        Page<OutboundTaskReport> page = service.getOutboundTaskReportPage(dto);
        return RestMessage.newInstance(true,"成功",page);
    }

    @ApiOperation(value = "根据上游系统任务单Id查询出库任务单回告", notes = "根据上游系统任务单Id查询出库任务单回告")
    @PostMapping("/getListByUpperSystemTaskId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "OutboundTaskReportDto", value = "出库任务单回告") })
    public RestMessage<List<OutboundTaskReport>> getListByUpperSystemTaskId(
            @RequestBody OutboundTaskReportDto dto) {
        List<OutboundTaskReport> list = service
                .getListByUpperSystemTaskId(dto.getUpperSystemTaskId());
        return RestMessage.newInstance(true, "成功", list);
    }

}
