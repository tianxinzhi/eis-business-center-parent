package com.prolog.eis.bc.controller.businesscenter;

import com.prolog.eis.bc.facade.dto.businesscenter.OutboundTaskHisDto;
import com.prolog.eis.bc.service.businesscenter.OutTaskHisManagerService;
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
@Api(tags = "出库任务单历史管理")
@RequestMapping("/outtaskhismanager")
public class OutTaskHisManagerController {
    @Autowired
    private OutTaskHisManagerService service;

    @ApiOperation(value = "出库任务单历史查询", notes = "出库任务单历史查询")
    @PostMapping("/getouttaskhis")
    @ApiImplicitParams({@ApiImplicitParam(name = "OutboundTaskHisDto", value = "出库历史任务单")})
    public RestMessage<Page<OutboundTaskHisDto>> getOuttaskHisPage (@RequestBody OutboundTaskHisDto dto){
        Page<OutboundTaskHisDto> page = service.getOuttaskHisPage(dto);
        return RestMessage.newInstance(true,"成功",page);
    }
}
