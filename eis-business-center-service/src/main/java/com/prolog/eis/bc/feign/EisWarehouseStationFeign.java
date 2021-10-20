package com.prolog.eis.bc.feign;

import com.prolog.eis.core.model.base.area.Station;
import com.prolog.eis.core.model.base.area.WhArea;
import com.prolog.framework.common.message.RestMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "${prolgo.service.warehouse:upcloud-base-wh-dev-mysql}")
public interface EisWarehouseStationFeign {

    @GetMapping("/station/findAllUnlockAndClaimStation")
    RestMessage<List<Station>> findAllUnlockAndClaimStation();

    @PostMapping("/eisWhArea/getAreaByLocation")
    RestMessage<WhArea> getAreaByLocation(@RequestParam(value = "location", required = false) String location);

}
