package com.prolog.eis.bc.feign.container;

import com.prolog.eis.core.model.biz.route.ContainerLocation;
import com.prolog.framework.common.message.RestMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: txz
 * @Date: 2021/9/24 10:00
 * @Desc: 远程调用容器位置接口服务
 */
@FeignClient("service-ai-eis-route-center")
public interface EisContainerLocationFeign {

    /**
     * 获取区域所有容器
     * @param areaKey 区域编号字段key(sourceArea...)
     * @param value 区域编号值
     * @return
     */
    @PostMapping("/api/v1/route/location/findByAreaNo")
    RestMessage<List<ContainerLocation>> findByAreaNo(@RequestParam("areaKey") String areaKey, @RequestParam("value")String value);

}
