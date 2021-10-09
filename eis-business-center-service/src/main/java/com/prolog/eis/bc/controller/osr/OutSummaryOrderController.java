package com.prolog.eis.bc.controller.osr;


import com.prolog.eis.bc.facade.dto.osr.OutSummaryOrderInfoDto;
import com.prolog.eis.bc.service.osr.OutboundSummaryOrderService;
import com.prolog.framework.common.message.RestMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 出库汇总任务单
 * 1.出库汇总任务单任务下发
 * 2.出库汇总任务单任务回告完成
 * 3.拆单调度
 * @author txz
 * @date 2021/09/23 09:21
 */
@RestController
@Api(tags = "出库汇总任务单")
@RequestMapping("out")
public class OutSummaryOrderController {

    @Autowired
    private OutboundSummaryOrderService service;

    @ApiOperation(value = "出库汇总任务单", notes = "出库汇总任务单")
    @PostMapping("/save-OutSummary-Order")
    @ApiImplicitParams({@ApiImplicitParam(name = "OutSummaryOrderInfoDto", value = "外来单据", required = false)})
    public RestMessage<String> createOutOrder(@RequestBody @Validated OutSummaryOrderInfoDto dto){
        try {
            return RestMessage.success("创建成功",service.createOutOrder(dto));
        } catch (Exception e) {
            e.printStackTrace();
            return RestMessage.error("创建异常");
        }

    }

}
