package com.prolog.eis.bc.controller.businesscenter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prolog.eis.bc.facade.dto.inbound.InboundTaskReportDto;
import com.prolog.eis.bc.facade.vo.inbound.InboundTaskReportVo;
import com.prolog.eis.bc.service.inbound.InboundTaskReportService;
import com.prolog.eis.common.util.JsonHelper;
import com.prolog.eis.component.algorithm.InterfaceDtoUtil;
import com.prolog.eis.core.model.biz.inbound.InboundTaskReport;
import com.prolog.framework.common.message.RestMessage;
import com.prolog.framework.core.pojo.Page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author: wuxl
 * @create: 2021-10-18 17:45
 * @Version: V1.0
 */
@RestController
@Api(tags = "业务中心-入库任务单回告")
@RequestMapping("/inboundTaskReport")
public class InboundTaskReportController {
    @Autowired
    private InboundTaskReportService inboundTaskReportService;

    @ApiOperation(value = "入库任务单回告查询", notes = "入库任务单回告查询")
    @PostMapping("/list")
    public RestMessage<Page<InboundTaskReportVo>> findList(@RequestBody InboundTaskReportDto dto) {
        if (InterfaceDtoUtil.checkObjAllFieldsIsNull(dto)) {
            return RestMessage.newInstance(false, "查询失败:参数异常");
        }
        Page<InboundTaskReportVo> page = inboundTaskReportService.listInboundTaskReportByPage(dto);
        return RestMessage.newInstance(true, "成功", page);
    }

    @ApiOperation(value = "查询全部出库任务单回告", notes = "查询全部出库任务单回告")
    @PostMapping("/findAll")
    public RestMessage<List<InboundTaskReport>> findAll() {
        List<InboundTaskReport> list = inboundTaskReportService.findAll();
        return RestMessage.newInstance(true, "成功", list);
    }

    @ApiOperation(value = "入库任务回告转历史", notes = "入库任务回告转历史")
    @PostMapping("/toCallbackHis")
    public RestMessage<String> toCallbackHis(@RequestBody String json) throws Exception {
        InboundTaskReport dto = JsonHelper.getObject(json, InboundTaskReport.class);
        RestMessage<String> restMessage;
        try {
            inboundTaskReportService.toCallbackHis(dto);
            restMessage = RestMessage.newInstance(true, "操作成功", null);
        } catch (Exception e) {
            restMessage = RestMessage.newInstance(false, "操作失败", e.getMessage());
        }
        return restMessage;
    }

    @ApiOperation(value = "入库通知失败", notes = "入库通知失败")
    @PostMapping("/toCallbackFail")
    public RestMessage<String> toCallbackFail(@RequestBody String json) throws Exception {
        InboundTaskReport dto = JsonHelper.getObject(json, InboundTaskReport.class);
        RestMessage<String> restMessage;
        try {
            inboundTaskReportService.toCallbackFail(dto);
            restMessage = RestMessage.newInstance(true, "操作成功", null);
        } catch (Exception e) {
            restMessage = RestMessage.newInstance(false, "操作失败", e.getMessage());
        }
        return restMessage;
    }

}
