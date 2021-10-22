package com.prolog.eis.bc.feign.container;

import com.prolog.eis.core.dto.inboundallot.InboundAllotAreaParamDto;
import com.prolog.eis.core.dto.inboundallot.InboundAllotAreaResultDto;
import com.prolog.eis.core.dto.route.CarryTaskCallbackDto;
import com.prolog.eis.core.model.biz.route.ContainerLocation;
import com.prolog.eis.router.vo.ContainerLocationVo;
import com.prolog.framework.common.message.RestMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/9/16
 **/
@FeignClient(value = "${prolog.service.route:service-ai-eis-route-center}")
public interface EisContainerRouteClient {

    /**
     * 通过容器号集合查询容器位置
     * @param containerList
     * @return
     */
    @PostMapping("/api/v1/route/location/findLocationByContainerList")
    RestMessage<List<ContainerLocationVo>> findLocationByContainerList(@RequestBody List<String> containerList);

    @PostMapping("/api/v1/route/carry/createTask")
    RestMessage<String> createCarry(@RequestBody String json) throws Exception;

    /**
     * 查询所有搬运任务回告数据
     * @return
     */
    @PostMapping("/api/v1/route/carry/findAllCallback")
    RestMessage<List<CarryTaskCallbackDto>> findAllCallback();

    @PostMapping("/api/v1/route/carry/toCallbackHisList")
    RestMessage<String> toCallbackHisList(@RequestBody List<CarryTaskCallbackDto> carryTaskCallbackDtos)throws Exception;

    @PostMapping("/api/v1/route/location/findContainerLocation")
    RestMessage<String> findContainerLocation(@RequestBody String json) throws Exception;

    @PostMapping("/api/v1/route/inbound/findArea")
    RestMessage<InboundAllotAreaResultDto> getAllotArea(@RequestBody InboundAllotAreaParamDto dto);

    /**
     * 查询某些区域的空闲托盘
     * @param areaNos 区域多个以英文,分隔
     * @return
     */
    @PostMapping("/api/v1/route/location/findFreeContainerByAreaNo")
    RestMessage<List<ContainerLocation>> findFreeContainerByAreaNo(
            @RequestParam(value = "areaNos", required = false) String areaNos);

}
