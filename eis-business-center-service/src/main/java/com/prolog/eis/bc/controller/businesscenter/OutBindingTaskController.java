package com.prolog.eis.bc.controller.businesscenter;

import com.prolog.eis.bc.facade.dto.businesscenter.OutBindingTaskDto;
import com.prolog.eis.bc.service.businesscenter.OutBindingTaskService;
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
}
