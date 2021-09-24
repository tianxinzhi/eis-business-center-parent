package com.prolog.eis.bc.feign.container;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.prolog.framework.common.message.RestMessage;

@FeignClient(value = "${prolog.service.route:service-ai-eis-route-center}")
public interface EisContainerLocationFeign {

    /**
     * Feign接口-查询已到达的容器数量
     * @param areaNo
     * @return
     * @throws Exception
     */
    @GetMapping("/api/v1/route/location/findArriveLxCount")
    RestMessage<Long> findArriveLxCount(@RequestParam(value = "areaNo", required = false) String areaNo);

    @GetMapping("/api/v1/route/location/findChuKuLxCount")
    RestMessage<Long> findChuKuLxCount(@RequestParam(value = "areaNo", required = false) String areaNo);

}
