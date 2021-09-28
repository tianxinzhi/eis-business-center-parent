package com.prolog.eis.bc.controller.policy;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prolog.eis.bc.facade.dto.policy.ContainerTaskStrategyDto;
import com.prolog.eis.bc.service.policy.ContainerTaskStgSaService;
import com.prolog.eis.bc.service.policy.ContainerTaskStgService;
import com.prolog.eis.bc.service.policy.ContainerTaskStgTaService;
import com.prolog.eis.core.model.ctrl.container.ContainerTaskStrategy;
import com.prolog.eis.core.model.ctrl.container.ContainerTaskStrategySourceArea;
import com.prolog.eis.core.model.ctrl.container.ContainerTaskStrategyTargetArea;
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
@Api(tags = "容器任务单策略")
@RequestMapping("containerTaskStg")
public class ContainerTaskStgController {

    @Autowired
    private ContainerTaskStgService containerTaskStgService;
    
    @Autowired
    private ContainerTaskStgSaService containerTaskStgSaService;
    
    @Autowired
    private ContainerTaskStgTaService containerTaskStgTaService;

    @ApiOperation(value = "容器任务单策略查询", notes = "容器任务单策略查询")
    @PostMapping("/get-container-task-stg")
    @ApiImplicitParams({@ApiImplicitParam(name = "getContainerTaskStg", value = "分页", required = false)})
    public RestMessage<Page<ContainerTaskStrategy>> getContainerTaskStg(@RequestBody ContainerTaskStrategyDto dto){
        Page<ContainerTaskStrategy> page = containerTaskStgService.getContainerTaskStg(dto);
        return RestMessage.newInstance(true,"成功",page);
    }
    
    @ApiOperation(value = "容器任务单策略新增", notes = "容器任务单策略新增")
    @PostMapping("/add-container-task-stg")
    @ApiImplicitParams({@ApiImplicitParam(name = "addContainerTaskStg", value = "新增", required = false)})
    public RestMessage<String> addContainerTaskStg(@RequestBody ContainerTaskStrategy dto){
        containerTaskStgService.addContainerTaskStg(dto);
        return RestMessage.newInstance(true,"成功",null);
    }

    @ApiOperation(value = "容器任务单策略修改", notes = "容器任务单策略修改")
    @PostMapping("/edit-container-task-stg")
    @ApiImplicitParams({@ApiImplicitParam(name = "editContainerTaskStg", value = "修改", required = false)})
    public RestMessage<String> editContainerTaskStg(@RequestBody ContainerTaskStrategy dto){
        containerTaskStgService.editContainerTaskStg(dto);
        return RestMessage.newInstance(true,"成功",null);
    }
    
    @ApiOperation(value = "容器任务单区域配置-起点", notes = "容器任务单区域配置-起点")
    @PostMapping("/detail-source-area")
    @ApiImplicitParams({@ApiImplicitParam(name = "detailSourceArea", value = "区域配置-起点", required = false)})
    public RestMessage<List<ContainerTaskStrategySourceArea>> detailSourceArea(@RequestBody ContainerTaskStrategyDto dto){
    	List<ContainerTaskStrategySourceArea> list = containerTaskStgSaService.detailSourceArea(dto);
        return RestMessage.newInstance(true,"成功",list);
    }
    
    @ApiOperation(value = "容器任务单区域配置-终点", notes = "容器任务单区域配置-终点")
    @PostMapping("/detail-target-area")
    @ApiImplicitParams({@ApiImplicitParam(name = "detailTargetArea", value = "区域配置-终点", required = false)})
    public RestMessage<List<ContainerTaskStrategyTargetArea>> detailTargetArea(@RequestBody ContainerTaskStrategyDto dto){
    	List<ContainerTaskStrategyTargetArea> list = containerTaskStgTaService.detailTargetArea(dto);
        return RestMessage.newInstance(true,"成功",list);
    }
    
    @ApiOperation(value = "容器任务单区域配置修改-起点", notes = "容器任务单区域配置修改-起点")
    @PostMapping("/edit-source-area")
    @ApiImplicitParams({@ApiImplicitParam(name = "editSourceArea", value = "区域配置修改-起点", required = false)})
    public RestMessage<String> editSourceArea(@RequestBody ContainerTaskStrategyDto dto){
    	containerTaskStgSaService.editSourceArea(dto);
        return RestMessage.newInstance(true,"成功",null);
    }
    
    @ApiOperation(value = "容器任务单区域配置修改-终点", notes = "容器任务单区域配置修改-终点")
    @PostMapping("/edit-target-area")
    @ApiImplicitParams({@ApiImplicitParam(name = "editTargetArea", value = "区域配置修改-终点", required = false)})
    public RestMessage<String> editTargetArea(@RequestBody ContainerTaskStrategyDto dto){
    	containerTaskStgTaService.editTargetArea(dto);
        return RestMessage.newInstance(true,"成功",null);
    }
    
    @ApiOperation(value = "容器任务单策略删除", notes = "容器任务单策略删除")
    @PostMapping("/delete-container-task-stg")
    @ApiImplicitParams({@ApiImplicitParam(name = "deleteContainerTaskStg", value = "删除", required = false)})
    public RestMessage<String> deleteContainerTaskStg(@RequestBody ContainerTaskStrategy dto){
        containerTaskStgService.deleteContainerTaskStg(dto);
        return RestMessage.newInstance(true,"成功",null);
    }
}
