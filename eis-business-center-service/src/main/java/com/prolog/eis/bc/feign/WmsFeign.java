package com.prolog.eis.bc.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.prolog.eis.bc.facade.dto.inbound.MasterInboundTaskDto;
import com.prolog.framework.common.message.RestMessage;

@FeignClient(value = "${prolog.service.inbound:UPCLOUD-BASE-WH-INBOUND}")
public interface WmsFeign {

    /**
     * 索取入库任务
     * @return 网络传输对象
     */
    @GetMapping("/wms/v1.0/pickInstockTask")
    RestMessage<MasterInboundTaskDto> pickInstockTask(
            @RequestParam(value = "container", required = false) String container,
            @RequestParam(value = "mtlOwnerId", required = false) String mtlOwnerId,
            @RequestParam(value = "warehouseId", required = false) String warehouseId);

}
