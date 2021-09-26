package com.prolog.eis.bc.feign.container;

import com.prolog.framework.common.message.RestMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author clarence_she
 * @Date 2021/8/19
 **/
@FeignClient(value = "${prolog.service.route:service-ai-eis-route-center}")
public interface CarryInterfaceFeign {

    @PostMapping("/api/v1/route/carry/createTask")
    RestMessage<String> createCarry(@RequestBody String json) throws Exception;

}
