package com.prolog.eis.bc.controller.policy;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prolog.eis.bc.facade.dto.policy.ContainerTaskStrategyDto;
import com.prolog.eis.bc.facade.dto.policy.OutStgDto;
import com.prolog.eis.bc.service.policy.OutStgSaService;
import com.prolog.eis.bc.service.policy.OutStgService;
import com.prolog.eis.bc.service.policy.OutStgTaService;
import com.prolog.eis.core.model.ctrl.container.ContainerTaskStrategySourceArea;
import com.prolog.eis.core.model.ctrl.container.ContainerTaskStrategyTargetArea;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategyConfig;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategySourceAreaConfig;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategyTargetStationConfig;
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
@Api(tags = "出库任务单策略配置")
@RequestMapping("outStg")
public class OutStgController {

    @Autowired
    private OutStgService outStgService;
    @Autowired
    private OutStgSaService outStgSaService;
    @Autowired
    private OutStgTaService outStgTaService;

    @ApiOperation(value = "出库任务单策略配置查询", notes = "出库任务单策略配置查询")
    @PostMapping("/get-out-stg")
    @ApiImplicitParams({@ApiImplicitParam(name = "getOutStg", value = "分页", required = false)})
    public RestMessage<Page<OutboundStrategyConfig>> getOutStg(@RequestBody OutStgDto dto){
        Page<OutboundStrategyConfig> page = outStgService.getOutStg(dto);
        return RestMessage.newInstance(true,"成功",page);
    }
    
    @ApiOperation(value = "出库任务单策略配置新增", notes = "出库任务单策略配置新增")
    @PostMapping("/add-out-stg")
    @ApiImplicitParams({@ApiImplicitParam(name = "addOutStg", value = "新增", required = false)})
    public RestMessage<String> addOutStg(@RequestBody OutboundStrategyConfig dto){
    	outStgService.addOutStg(dto);
        return RestMessage.newInstance(true,"成功",null);
    }

    @ApiOperation(value = "出库任务单策略配置修改", notes = "出库任务单策略配置修改")
    @PostMapping("/edit-out-stg")
    @ApiImplicitParams({@ApiImplicitParam(name = "editOutStg", value = "修改", required = false)})
    public RestMessage<String> editOutStg(@RequestBody OutboundStrategyConfig dto){
    	outStgService.editOutStg(dto);
        return RestMessage.newInstance(true,"成功",null);
    }
    
    @ApiOperation(value = "出库任务单策略区域配置-起点", notes = "出库任务单策略区域配置-起点")
    @PostMapping("/detail-source-area")
    @ApiImplicitParams({@ApiImplicitParam(name = "detailSourceArea", value = "区域配置-起点", required = false)})
    public RestMessage<List<OutboundStrategySourceAreaConfig>> detailSourceArea(@RequestBody OutStgDto dto){
    	List<OutboundStrategySourceAreaConfig> list = outStgSaService.detailSourceArea(dto);
        return RestMessage.newInstance(true,"成功",list);
    }
    
    @ApiOperation(value = "出库任务单策略区域配置-终点", notes = "出库任务单策略区域配置-终点")
    @PostMapping("/detail-Target-area")
    @ApiImplicitParams({@ApiImplicitParam(name = "detailTargetArea", value = "区域配置-终点", required = false)})
    public RestMessage<List<OutboundStrategyTargetStationConfig>> detailTargetArea(@RequestBody OutStgDto dto){
    	List<OutboundStrategyTargetStationConfig> list = outStgTaService.detailTargetArea(dto);
        return RestMessage.newInstance(true,"成功",list);
    }
    
    @ApiOperation(value = "出库任务单策略区域配置修改-起点", notes = "出库任务单策略区域配置修改-起点")
    @PostMapping("/edit-source-area")
    @ApiImplicitParams({@ApiImplicitParam(name = "editSourceArea", value = "区域配置修改-起点", required = false)})
    public RestMessage<String> editSourceArea(@RequestBody OutStgDto dto){
    	outStgSaService.editSourceArea(dto);
        return RestMessage.newInstance(true,"成功",null);
    }
    
    @ApiOperation(value = "出库任务单策略区域配置修改-终点", notes = "出库任务单策略区域配置修改-终点")
    @PostMapping("/edit-Target-area")
    @ApiImplicitParams({@ApiImplicitParam(name = "editTargetArea", value = "区域配置修改-终点", required = false)})
    public RestMessage<String> editTargetArea(@RequestBody OutStgDto dto){
    	outStgTaService.editTargetArea(dto);
        return RestMessage.newInstance(true,"成功",null);
    }
    
    @ApiOperation(value = "出库任务单策略配置删除", notes = "出库任务单策略配置删除")
    @PostMapping("/delete-out-stg")
    @ApiImplicitParams({@ApiImplicitParam(name = "deleteOutStg", value = "删除", required = false)})
    public RestMessage<String> deleteOutStg(@RequestBody OutboundStrategyConfig dto){
    	outStgService.deleteOutStg(dto);
        return RestMessage.newInstance(true,"成功",null);
    }
}
