package com.prolog.eis.bc.feign.container;

import com.prolog.eis.router.vo.ContainerLocationVo;
import com.prolog.framework.common.message.RestMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/9/16
 **/
@FeignClient(value = "${prolog.service.route:service-ai-eis-route-center}")
public interface EisContainerRouteClient {

    @PostMapping("/api/v1/route/location/findLocationByContainerList")
    public RestMessage<List<ContainerLocationVo>> findLocationByContainerList(@RequestBody List<String> containerList);
}
