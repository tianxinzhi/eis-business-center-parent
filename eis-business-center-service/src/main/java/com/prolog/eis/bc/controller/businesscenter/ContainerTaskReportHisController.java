package com.prolog.eis.bc.controller.businesscenter;

import com.prolog.eis.bc.facade.dto.businesscenter.ContainerTaskReportHisDto;
import com.prolog.eis.core.model.biz.container.ContainerTaskReportHis;
import com.prolog.eis.bc.service.businesscenter.ContainerTaskReportHisService;
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
@Api(tags = "容器任务单回告历史")
@RequestMapping("/containertaskreporthis")
public class ContainerTaskReportHisController {
    @Autowired
    private ContainerTaskReportHisService containerTaskReportHisService;

    @ApiOperation(value = "容器任务单回告历史查询", notes = "容器任务单回告历史查询")
    @PostMapping("/getcontainertaskhisreport")
    @ApiImplicitParams({@ApiImplicitParam(name = "ContainerTaskReportHisDto", value = "容器任务单历史回告")})
    public RestMessage<Page<ContainerTaskReportHis>> getContainerTaskReportHisPage (@RequestBody ContainerTaskReportHisDto dto){
        Page<ContainerTaskReportHis> page = containerTaskReportHisService.getContainerTaskReportHisPage(dto);
        return RestMessage.newInstance(true,"成功",page);
    }
}
