package com.prolog.eis.bc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prolog.eis.bc.facade.dto.businesscenter.OutboundTaskDto;
import com.prolog.eis.bc.service.outboundtask.OutboundTaskService;
import com.prolog.eis.core.model.biz.outbound.OutboundTask;
import com.prolog.framework.common.message.RestMessage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@Api(tags = "拣货单")
@RequestMapping("/outboundtask")
@Slf4j
public class OutboundTaskController {

    @Autowired
    private OutboundTaskService outboundTaskService;

    @ApiOperation(value = "生成拣选单", notes = "生成拣选单")
    @GetMapping("/composeAndGenerateOutbound")
    public RestMessage<Boolean> composeAndGenerateOutbound() {
        try {
            outboundTaskService.composeAndGenerateOutbound();
            return RestMessage.newInstance(true, "成功", Boolean.TRUE);
        } catch (Exception ex) {
            log.error(ex.toString());
            return RestMessage.newInstance(false, ex.toString());
        }
    }

    @ApiOperation(value = "生成拣选单回告和历史", notes = "生成拣选单回告和历史")
    @GetMapping("/genOutboundRpAndHis")
    public RestMessage<Boolean> genOutboundRpAndHis() {
        try {
            outboundTaskService.genOutboundRpAndHis();
            return RestMessage.newInstance(true, "成功", Boolean.TRUE);
        } catch (Exception ex) {
            log.error(ex.toString());
            return RestMessage.newInstance(false, ex.toString());
        }
    }

    @ApiOperation(value = "根据上游系统任务单Id查询出库任务单", notes = "根据上游系统任务单Id查询出库任务单")
    @PostMapping("/getListByUpperSystemTaskId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "OutboundTaskDto", value = "出库任务单") })
    public RestMessage<List<OutboundTask>> getListByUpperSystemTaskId(
            @RequestBody OutboundTaskDto dto) {
        List<OutboundTask> list = outboundTaskService
                .getListByUpperSystemTaskId(dto.getUpperSystemTaskId());
        return RestMessage.newInstance(true, "成功", list);
    }

}
