package com.prolog.eis.bc.feign.container;

import com.prolog.eis.core.model.ctrl.area.PortInfo;
import com.prolog.framework.common.message.RestMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/9/16
 **/
@FeignClient(value = "${prolog.service.control:upcloud-base-wh-control}")
public interface EisControllerClient {

    /**
     * 根据区域获取出入口资料
     *
     * @param areaNo
     * @return
     */
    @PostMapping("/eisPortInfoService/getPortByArea")
    RestMessage<PortInfo> getPortByArea(@RequestParam(value = "areaNo", required = false) String areaNo);
}
