package com.prolog.eis.bc.controller.businesscenter;

import com.prolog.eis.bc.facade.dto.inbound.InboundTaskHisDto;
import com.prolog.eis.bc.facade.vo.inbound.InboundTaskHisVo;
import com.prolog.eis.bc.service.inbound.InboundTaskService;
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
@Api(tags = "业务中心-入库任务单历史")
@RequestMapping("/inboundTaskHis")
public class InboundTaskHisController {
    @Autowired
    private InboundTaskService inboundTaskService;

    @ApiOperation(value = "入库任务单历史查询", notes = "入库任务单历史查询")
    @PostMapping("/list")
    public RestMessage<Page<InboundTaskHisVo>> findList(@RequestBody InboundTaskHisDto dto) {
        if (InterfaceDtoUtil.checkObjAllFieldsIsNull(dto)) {
            return RestMessage.newInstance(false, "查询失败:参数异常");
        }
        Page<InboundTaskHisVo> page = inboundTaskService.listInboundTaskHisByPage(dto);
        return RestMessage.newInstance(true, "成功", page);
    }
}
