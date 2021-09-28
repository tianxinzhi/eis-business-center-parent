package com.prolog.eis.bc.controller.policy;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prolog.eis.bc.facade.dto.policy.OutSplitStgDto;
import com.prolog.eis.bc.facade.dto.policy.OutStgDto;
import com.prolog.eis.bc.service.policy.OutSplitStgService;
import com.prolog.eis.bc.service.sscdtl.OutboundSplitStrategyDetailConfigService;
import com.prolog.eis.core.model.ctrl.outbound.OutboundSplitStrategyConfig;
import com.prolog.eis.core.model.ctrl.outbound.OutboundSplitStrategyDetailConfig;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategySourceAreaConfig;
import com.prolog.framework.common.message.RestMessage;
import com.prolog.framework.core.pojo.Page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @author hzw
 * @date 2021/09/27
 */
@RestController
@Api(tags = "出库任务汇总单拆单策略配置")
@RequestMapping("outSplitStg")
public class OutSplitStgController {

    @Autowired
    private OutSplitStgService outSplitStgService;
    @Autowired
    private OutboundSplitStrategyDetailConfigService outboundSplitStrategyDetailConfigService;

    @ApiOperation(value = "出库任务汇总单拆单策略配置查询", notes = "出库任务汇总单拆单策略配置查询")
    @PostMapping("/get-out-split-stg")
    @ApiImplicitParams({@ApiImplicitParam(name = "getOutSplitStg", value = "分页", required = false)})
    public RestMessage<Page<OutboundSplitStrategyConfig>> getOutSplitStg(@RequestBody OutSplitStgDto dto){
        Page<OutboundSplitStrategyConfig> page = outSplitStgService.getOutSplitStg(dto);
        return RestMessage.newInstance(true,"成功",page);
    }
    
    @ApiOperation(value = "出库任务汇总单拆单策略配置新增", notes = "出库任务汇总单拆单策略配置新增")
    @PostMapping("/add-out-split-stg")
    @ApiImplicitParams({@ApiImplicitParam(name = "addOutSplitStg", value = "新增", required = false)})
    public RestMessage<String> addOutSplitStg(@RequestBody OutboundSplitStrategyConfig dto){
    	outSplitStgService.addOutSplitStg(dto);
        return RestMessage.newInstance(true,"成功",null);
    }

    @ApiOperation(value = "出库任务汇总单拆单策略配置修改", notes = "出库任务汇总单拆单策略配置修改")
    @PostMapping("/edit-out-stg")
    @ApiImplicitParams({@ApiImplicitParam(name = "editOutStg", value = "修改", required = false)})
    public RestMessage<String> editOutSplitStg(@RequestBody OutboundSplitStrategyConfig dto){
    	outSplitStgService.editOutSplitStg(dto);
        return RestMessage.newInstance(true,"成功",null);
    }
    
    @ApiOperation(value = "出库任务汇总单拆单策略配置-明细", notes = "出库任务汇总单拆单策略配置-明细")
    @PostMapping("/detail-out-split")
    @ApiImplicitParams({@ApiImplicitParam(name = "detailOutSplit", value = "明细", required = false)})
    public RestMessage<List<OutboundSplitStrategyDetailConfig>> detailOutSplit(@RequestBody OutSplitStgDto dto){
    	List<OutboundSplitStrategyDetailConfig> list = outboundSplitStrategyDetailConfigService.detailOutSplit(dto);
        return RestMessage.newInstance(true,"成功",list);
    }
    
    @ApiOperation(value = "出库任务汇总单拆单策略配置-明细修改", notes = "出库任务汇总单拆单策略配置-明细修改")
    @PostMapping("/edit-out-split")
    @ApiImplicitParams({@ApiImplicitParam(name = "editOutSplit", value = "明细修改", required = false)})
    public RestMessage<String> editOutSplit(@RequestBody OutSplitStgDto dto){
    	outboundSplitStrategyDetailConfigService.editOutSplit(dto);
        return RestMessage.newInstance(true,"成功",null);
    }
    
    @ApiOperation(value = "出库任务汇总单拆单策略配置删除", notes = "出库任务汇总单拆单策略配置删除")
    @PostMapping("/delete-out-stg")
    @ApiImplicitParams({@ApiImplicitParam(name = "deleteOutStg", value = "删除", required = false)})
    public RestMessage<String> deleteOutStg(@RequestBody OutboundSplitStrategyConfig dto){
    	outSplitStgService.deleteOutSplitStg(dto);
        return RestMessage.newInstance(true,"成功",null);
    }
}
