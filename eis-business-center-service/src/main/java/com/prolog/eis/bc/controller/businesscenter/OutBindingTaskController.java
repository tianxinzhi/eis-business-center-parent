package com.prolog.eis.bc.controller.businesscenter;

import com.prolog.eis.bc.facade.dto.businesscenter.OutBindingTaskDto;
import com.prolog.eis.bc.service.businesscenter.OutBindingTaskService;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskBindDetail;
import com.prolog.framework.common.message.RestMessage;
import com.prolog.framework.core.pojo.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "出库任务单容器绑定")
@RequestMapping("/outbindingtask")
public class OutBindingTaskController {
    @Autowired
    private OutBindingTaskService outBindingTaskService;


    @ApiOperation(value = "出库任务单容器绑定查询", notes = "出库任务单容器绑定查询")
    @PostMapping("/getoutbindingtask")
    @ApiImplicitParams({@ApiImplicitParam(name = "OutBindingTaskDto", value = "出库任务单容器绑定")})
    public RestMessage<Page<OutBindingTaskDto>> getOutBindingTaskPage (@RequestBody OutBindingTaskDto dto){
        Page<OutBindingTaskDto> page = outBindingTaskService.getOuttaskHisPage(dto);
        return RestMessage.newInstance(true,"成功",page);
    }

    @ApiOperation(value = "通过出库任务单容器绑定Id查询明细集合", notes = "通过出库任务单容器绑定Id查询明细集合")
    @GetMapping("/getOutBindingTaskDetail")
    public RestMessage<List<OutboundTaskBindDetail>> getOutBindingTaskDetail (@RequestParam String id){
        List<OutboundTaskBindDetail> page = outBindingTaskService.getOutBindingTaskDetail(id);
        return RestMessage.newInstance(true,"成功",page);
    }
}
