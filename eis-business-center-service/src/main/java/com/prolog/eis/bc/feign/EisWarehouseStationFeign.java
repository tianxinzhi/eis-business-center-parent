package com.prolog.eis.bc.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.prolog.eis.core.dto.route.WhLocatorDto;
import com.prolog.eis.core.model.base.area.Station;
import com.prolog.eis.core.model.base.area.WhArea;
import com.prolog.framework.common.message.RestMessage;

@FeignClient(value = "${prolog.service.warehouse:upcloud-base-wh-dev-mysql}")
public interface EisWarehouseStationFeign {

    @GetMapping("/station/findAllUnlockAndClaimStation")
    RestMessage<List<Station>> findAllUnlockAndClaimStation();

    @PostMapping("/eisWhArea/getAreaByLocation")
    RestMessage<WhArea> getAreaByLocation(@RequestParam(value = "location", required = false) String location);

    @PostMapping("/eisLocator/getWhLocatorListByLocationNos")
    RestMessage<List<WhLocatorDto>> getWhLocatorListByLocationNos(
            @RequestParam(value = "locationNos", required = false) String locationNos);
}
