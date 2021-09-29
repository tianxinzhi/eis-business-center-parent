package com.prolog.eis.bc.controller.businesscenter;

import com.prolog.eis.bc.facade.dto.businesscenter.BusiContainerTaskDto;
import com.prolog.eis.bc.service.businesscenter.BusiContainerTaskService;
import com.prolog.eis.core.model.biz.container.ContainerTask;
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
@Api(tags = "业务中心-容器任务单管理")
@RequestMapping("/busicontainertask")
public class BusiContainerTaskController {
    @Autowired
    private BusiContainerTaskService containerTaskService;
    @ApiOperation(value = "业务中心-容器任务单管理", notes = "业务中心-容器任务单管理")
    @PostMapping("/getcontainertask")
    @ApiImplicitParams({@ApiImplicitParam(name = "BusiContainerTaskDto", value = "容器任务单管理")})
    public RestMessage<Page<BusiContainerTaskDto>> getBusiContainertask (@RequestBody BusiContainerTaskDto dto){
        Page<BusiContainerTaskDto> page = containerTaskService.getBusiContainerTask(dto);
        return RestMessage.newInstance(true,"成功",page);
    }

    @ApiOperation(value = "容器任务单管理更新优先级", notes = "容器任务单管理更新优先级")
    @PostMapping("/updatepriority")
    @ApiImplicitParams({@ApiImplicitParam(name = "BusiContainerTaskDto", value = "容器任务单管理")})
    public RestMessage updatepriority (@RequestBody ContainerTask dto){
        containerTaskService.updatePriority(dto.getId(),dto.getPriority());
        return RestMessage.newInstance(true, "成功", null);
    }

}
