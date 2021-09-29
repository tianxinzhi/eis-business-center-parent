package com.prolog.eis.bc.controller.businesscenter;

import com.prolog.eis.bc.facade.dto.businesscenter.OutBindingTaskHisDto;
import com.prolog.eis.bc.service.businesscenter.OutBindingTaskHisService;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskBindDetail;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskBindDetailHis;
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

    @ApiOperation(value = "通过容器单Id查询明细集合", notes = "通过容器单Id查询明细集合")
    @GetMapping("/getOutBindingTaskDetail")
    public RestMessage<List<OutboundTaskBindDetailHis>> getOutBindingTaskDetail (@RequestParam String id){
        List<OutboundTaskBindDetailHis> page = outBindingTaskHisService.getOutBindingTaskDetail(id);
        return RestMessage.newInstance(true,"成功",page);
    }
}
