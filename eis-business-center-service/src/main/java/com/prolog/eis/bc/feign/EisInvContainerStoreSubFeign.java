package com.prolog.eis.bc.feign;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.prolog.framework.common.message.RestMessage;
import com.prolog.upcloud.base.inventory.vo.EisInvContainerStoreVo;

@FeignClient(value = "${prolog.service.inventory:upcloud-base-inventory-mysql}")
public interface EisInvContainerStoreSubFeign {

    @GetMapping("/eisInvSub/findSumQtyGroupByLotId")
    RestMessage<Map<String, Integer>> findSumQtyGroupByLotId();

    @GetMapping("/eisInvSub/findSumQtyGroupByItemId")
    RestMessage<Map<String, Integer>> findSumQtyGroupByItemId();

    @PostMapping("/eisInvSub/findByContainerNo")
    RestMessage<List<EisInvContainerStoreVo>> findByContainerNo(
            @RequestParam(value = "containerNo", required = false) String containerNo);

}
