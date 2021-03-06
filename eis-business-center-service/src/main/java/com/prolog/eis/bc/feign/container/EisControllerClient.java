package com.prolog.eis.bc.feign.container;

import com.prolog.eis.core.model.ctrl.area.PortInfo;
import com.prolog.framework.common.message.RestMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
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

    /**
     * 获取系统参数
     *
     * @param regCode
     * @return
     */
    @PostMapping("/registry/value")
    RestMessage<String> getValueByCode(@RequestParam(value = "regCode", required = false) String regCode);

    /**
     * 根据开关编码获取开关
     *
     * @param switchCode
     * @return
     */
    @GetMapping("/dispatchswitch/getSwitchByCode")
    RestMessage<Boolean> getSwitchByCode(@RequestParam(value = "switchCode", required = true) String switchCode);
}
