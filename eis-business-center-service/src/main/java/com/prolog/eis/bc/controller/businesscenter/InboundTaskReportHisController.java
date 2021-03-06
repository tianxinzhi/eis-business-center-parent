package com.prolog.eis.bc.controller.businesscenter;

import com.prolog.eis.bc.facade.dto.inbound.InboundTaskReportHisDto;
import com.prolog.eis.bc.facade.vo.inbound.InboundTaskReportHisVo;
import com.prolog.eis.bc.service.inbound.InboundTaskReportService;
import com.prolog.eis.component.algorithm.InterfaceDtoUtil;
import com.prolog.framework.common.message.RestMessage;
import com.prolog.framework.core.pojo.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: wuxl
 * @create: 2021-10-18 17:45
 * @Version: V1.0
 */
@RestController
@Api(tags = "业务中心-入库任务单回告历史")
@RequestMapping("/inboundTaskReportHis")
public class InboundTaskReportHisController {
    @Autowired
    private InboundTaskReportService inboundTaskReportService;

    @ApiOperation(value = "入库任务单回告历史查询", notes = "入库任务单回告历史查询")
    @PostMapping("/list")
    public RestMessage<Page<InboundTaskReportHisVo>> findList(@RequestBody InboundTaskReportHisDto dto) {
        if (InterfaceDtoUtil.checkObjAllFieldsIsNull(dto)) {
            return RestMessage.newInstance(false, "查询失败:参数异常");
        }
        Page<InboundTaskReportHisVo> page = inboundTaskReportService.listInboundTaskReportHisByPage(dto);
        return RestMessage.newInstance(true, "成功", page);
    }
}
