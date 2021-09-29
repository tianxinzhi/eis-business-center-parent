package com.prolog.eis.bc.controller.businesscenter;

import com.prolog.eis.bc.facade.dto.businesscenter.OutboundTaskDto;
import com.prolog.eis.bc.service.businesscenter.OutTaskManagerService;
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
@Api(tags = "出库任务单管理")
@RequestMapping("/outtaskmanager")
public class OutTaskManagerController {
    @Autowired
    private OutTaskManagerService service;

    @ApiOperation(value = "出库任务单管理查询", notes = "出库任务单管理查询")
    @PostMapping("/getouttask")
    @ApiImplicitParams({@ApiImplicitParam(name = "outboundTaskDetailDto", value = "出库任务单")})
    public RestMessage<Page<OutboundTaskDto>> getOuttaskPage (@RequestBody OutboundTaskDto dto){
        Page<OutboundTaskDto> page = service.getOuttaskPage(dto);
        return RestMessage.newInstance(true,"成功",page);
    }

    @ApiOperation(value = "出库任务单管理更新优先级", notes = "出库任务单管理更新优先级")
    @PostMapping("/updatepriority")
    @ApiImplicitParams({@ApiImplicitParam(name = "outboundTaskDetailDto", value = "出库任务单")})
    public RestMessage updatepriority (@RequestBody OutboundTaskDto dto){
        service.updatePriority(dto.getId(),dto.getPriority());
        return RestMessage.newInstance(true, "成功", null);
    }
}
