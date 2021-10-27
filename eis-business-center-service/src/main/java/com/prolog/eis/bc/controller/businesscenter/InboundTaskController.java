package com.prolog.eis.bc.controller.businesscenter;

import com.prolog.eis.bc.facade.dto.inbound.InboundTaskDto;
import com.prolog.eis.bc.facade.vo.inbound.InboundTaskVo;
import com.prolog.eis.bc.service.inbound.InboundDispatch;
import com.prolog.eis.bc.service.inbound.InboundTaskService;
import com.prolog.eis.common.util.JsonHelper;
import com.prolog.eis.component.algorithm.InterfaceDtoUtil;
import com.prolog.eis.core.model.biz.inbound.InboundTask;
import com.prolog.eis.inter.dto.mcs.ZxMcsInBoundResponseDto;
import com.prolog.framework.common.message.RestMessage;
import com.prolog.framework.core.pojo.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: wuxl
 * @create: 2021-10-18 17:45
 * @Version: V1.0
 */
@RestController
@Api(tags = "业务中心-入库任务单")
@RequestMapping("/inboundTask")
@Slf4j
public class InboundTaskController {
    @Autowired
    private InboundTaskService inboundTaskService;
    @Autowired
    private InboundDispatch inboundDispatch;

    @ApiOperation(value = "入库任务单查询", notes = "入库任务单查询")
    @PostMapping("/list")
    public RestMessage<Page<InboundTaskVo>> findList(@RequestBody InboundTaskDto dto) {
        if (InterfaceDtoUtil.checkObjAllFieldsIsNull(dto)) {
            return RestMessage.newInstance(false, "查询失败:参数异常");
        }
        Page<InboundTaskVo> page = inboundTaskService.listInboundTaskByPage(dto);
        return RestMessage.newInstance(true, "成功", page);
    }

    @ApiOperation(value = "入库任务单取消", notes = "入库任务单取消")
    @PostMapping("/cancel")
    public RestMessage<Page<InboundTaskVo>> cancelTask(@RequestBody String json) throws Exception {
        InboundTask dto = JsonHelper.getObject(json, InboundTask.class);
        if (null == dto || StringUtils.isEmpty(dto.getUpperSystemTaskId())) {
            return RestMessage.newInstance(false, "取消失败:参数异常");
        }
        inboundTaskService.cancelTask(dto);
        return RestMessage.newInstance(true, "取消成功", null);
    }

    @ApiOperation(value = "入库申请(指定容器)", notes = "入库申请(指定容器)")
    @PostMapping("/apply/container")
    public RestMessage<String> applyContainer(@RequestBody String json) throws Exception {
        ZxMcsInBoundResponseDto dto = JsonHelper.getObject(json, ZxMcsInBoundResponseDto.class);
        if (null == dto) {
            return RestMessage.newInstance(false, "入库申请失败:参数异常");
        }
        inboundTaskService.applyContainer(dto);
        return RestMessage.newInstance(true, "入库申请成功", null);
    }

    @ApiOperation(value = "根据入库任务Id查询", notes = "根据入库任务Id查询")
    @PostMapping("/getListByIdList")
    public RestMessage<List<InboundTask>> getListByIdList(
            @RequestBody String json) throws Exception {
        log.info("getListByIdList called, param:{}", json);
        List<String> taskIdList = JsonHelper.getStringList("json");
        List<InboundTask> result = inboundTaskService.getListByIdList(taskIdList);
        return RestMessage.newInstance(true, "成功", result);
    }


    @ApiOperation(value = "入库调度", notes = "入库调度")
    @PostMapping("/dispatch")
    public RestMessage<String> inboundDispatch() throws Exception {
        inboundDispatch.inboundSchedule();
        return RestMessage.newInstance(true, "入库申请成功", null);
    }
}
