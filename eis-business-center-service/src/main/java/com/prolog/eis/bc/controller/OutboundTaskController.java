package com.prolog.eis.bc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prolog.eis.bc.service.outboundtask.OutboundTaskService;
import com.prolog.framework.common.message.RestMessage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

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
        }catch (Exception ex){
            log.error(ex.toString());
            return RestMessage.newInstance(false, ex.toString());
        }
    }

}
