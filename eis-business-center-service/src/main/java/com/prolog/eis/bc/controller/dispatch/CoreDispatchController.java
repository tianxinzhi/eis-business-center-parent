package com.prolog.eis.bc.controller.dispatch;

import com.prolog.eis.bc.service.dispatch.OutboundDispatchService;
import com.prolog.framework.common.message.RestMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/10/21
 **/
@RestController
@Api(tags = "出库调度定时器")
@RequestMapping("/coreDispatch")
@Slf4j
public class CoreDispatchController {

    @Autowired
    private OutboundDispatchService outboundDispatchService;

    @ApiOperation(value = "出库", notes = "出库")
    @GetMapping("/out")
    public RestMessage<Boolean> composeAndGenerateOutbound() {
        try {
            outboundDispatchService.coreDispatch();
            return RestMessage.newInstance(true, "成功", Boolean.TRUE);
        } catch (Exception ex) {
            log.error(ex.toString());
            return RestMessage.newInstance(false, ex.toString());
        }
    }

}
