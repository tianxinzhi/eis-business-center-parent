package com.prolog.eis.bc.controller.businesscenter;

import com.prolog.eis.bc.facade.dto.businesscenter.OutBindingTaskHisDto;
import com.prolog.eis.bc.service.businesscenter.OutBindingTaskHisService;
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
@Api(tags = "出库任务单容器绑定历史查询")
@RequestMapping("/outbindingtaskhis")
public class OutBindingTaskHisController {
    @Autowired
    private OutBindingTaskHisService outBindingTaskHisService;

    @ApiOperation(value = "出库任务单容器绑定历史查询", notes = "出库任务单容器绑定历史查询")
    @PostMapping("/getoutbindingtaskhis")
    @ApiImplicitParams({@ApiImplicitParam(name = "OutBindingTaskHisDto", value = "出库任务单容器绑定历史")})
    public RestMessage<Page<OutBindingTaskHisDto>> getOutBindingTaskPage (@RequestBody OutBindingTaskHisDto dto){
        Page<OutBindingTaskHisDto> page = outBindingTaskHisService.getOutBindingTaskHisPage(dto);
        return RestMessage.newInstance(true,"成功",page);
    }
}
