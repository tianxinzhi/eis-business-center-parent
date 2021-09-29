package com.prolog.eis.bc.controller.businesscenter;

import com.prolog.eis.bc.facade.dto.businesscenter.ContainerTaskReportDto;
import com.prolog.eis.core.model.biz.container.ContainerTaskReport;
import com.prolog.eis.bc.service.businesscenter.ContainerTaskReportService;
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
@Api(tags = "容器任务单回告")
@RequestMapping("/containertaskreport")
public class ContainerTaskReportController {
    @Autowired
    private ContainerTaskReportService containerTaskReportService;

    @ApiOperation(value = "容器任务单回告查询", notes = "容器任务单回告查询")
    @PostMapping("/getcontainertaskreport")
    @ApiImplicitParams({@ApiImplicitParam(name = "ContainerTaskReportDto", value = "容器任务单回告")})
    public RestMessage<Page<ContainerTaskReport>> getOrderPoolPage (@RequestBody ContainerTaskReportDto dto){
        Page<ContainerTaskReport> page = containerTaskReportService.getContainerTaskReportPage(dto);
        return RestMessage.newInstance(true,"成功",page);
    }
}
