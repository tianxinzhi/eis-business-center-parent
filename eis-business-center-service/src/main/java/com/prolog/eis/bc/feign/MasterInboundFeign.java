package com.prolog.eis.bc.feign;

import com.prolog.eis.bc.facade.dto.inbound.MasterInboundTaskDto;
import com.prolog.framework.common.message.RestMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "${prolog.service.xt:service-ai-eis-xt-center}")
public interface MasterInboundFeign {

    @PostMapping("/inbound/getInboundTaskFromWms")
    RestMessage<MasterInboundTaskDto> inboundTask(@RequestBody String json);
}
