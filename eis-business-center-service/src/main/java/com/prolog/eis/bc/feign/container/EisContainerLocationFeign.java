package com.prolog.eis.bc.feign.container;

import com.prolog.eis.core.model.biz.route.ContainerLocation;
import com.prolog.framework.common.message.RestMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: txz
 * @Date: 2021/9/24 10:00
 * @Desc: 远程调用容器位置接口服务
 */
@FeignClient(value = "${prolog.service.route:service-ai-eis-route-center}")
public interface EisContainerLocationFeign {

    /**
     * 获取区域所有容器
     * @param json 区域编号值
     * @return
     */
    @PostMapping("/api/v1/route/location/findByAreaNo")
    RestMessage<List<ContainerLocation>> findByAreaNo(@RequestBody String json);


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
