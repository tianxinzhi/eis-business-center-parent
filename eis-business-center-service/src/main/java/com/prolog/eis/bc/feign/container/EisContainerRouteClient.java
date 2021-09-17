package com.prolog.eis.bc.feign.container;

import com.prolog.eis.router.vo.ContainerLocationVo;
import com.prolog.framework.common.message.RestMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/9/16
 **/
@FeignClient(value = "${prolog.service.route}")
public interface EisContainerRouteClient {

    public RestMessage<List<ContainerLocationVo>> findLocationByContainerList(@RequestBody List<String> containerList);
}
