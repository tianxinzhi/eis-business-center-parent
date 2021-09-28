package com.prolog.eis.bc.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.prolog.eis.core.model.base.area.Station;
import com.prolog.framework.common.message.RestMessage;

@FeignClient(value = "${prolgo.service.warehouse:upcloud-base-wh-dev-mysql}")
public interface EisWarehouseStationFeign {

    @GetMapping("/station/findAllUnlockAndClaimStation")
    RestMessage<List<Station>> findAllUnlockAndClaimStation();

}
