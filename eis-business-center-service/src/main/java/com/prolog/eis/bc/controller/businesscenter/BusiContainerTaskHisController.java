package com.prolog.eis.bc.controller.businesscenter;

import com.prolog.eis.bc.facade.dto.businesscenter.BusiContainerTaskHisDto;
import com.prolog.eis.bc.service.businesscenter.BusiContainerTaskHisService;
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
@Api(tags = "业务中心-容器任务单历史管理")
@RequestMapping("/busicontainertaskhis")
public class BusiContainerTaskHisController {
    @Autowired
    private BusiContainerTaskHisService busiContainerTaskHisService;
    @ApiOperation(value = "业务中心-容器任务单历史管理", notes = "业务中心-容器任务单历史管理")
    @PostMapping("/getcontainertaskhis")
    @ApiImplicitParams({@ApiImplicitParam(name = "OutBindingTaskDto", value = "出库任务单容器历史绑定")})
    public RestMessage<Page<BusiContainerTaskHisDto>> getBusiContainertask (@RequestBody BusiContainerTaskHisDto dto){
        Page<BusiContainerTaskHisDto> page = busiContainerTaskHisService.getBusiContainerTask(dto);
        return RestMessage.newInstance(true,"成功",page);
    }
}
