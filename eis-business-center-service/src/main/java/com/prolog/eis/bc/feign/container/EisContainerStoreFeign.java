package com.prolog.eis.bc.feign.container;

import com.prolog.framework.common.message.RestMessage;
import com.prolog.upcloud.base.inventory.dto.EisSelectorInv;
import com.prolog.upcloud.base.inventory.vo.EisInvContainerStoreVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author clarence_she
 * @Date 2021/9/15
 **/
@FeignClient("${prolog.service.inventory:upcloud-base-inventory-mysql}")
public interface EisContainerStoreFeign {

    @PostMapping("/eisInv/findByItemOrLotId")
    RestMessage<List<EisInvContainerStoreVo>> findByItemOrLotId(@RequestBody EisSelectorInv eisSelectorInv);

    /**
     * 根据容器编号获取库存
     *
     * @param containerNos
     * @return
     */
    @PostMapping("/eisInv/findByContainerNos")
    RestMessage<List<EisInvContainerStoreVo>> findByContainerNos(@RequestBody List<String> containerNos);

    /**
     * 生成库存
     *
     * @param containerStoreVo
     * @return
     */
    @PostMapping("/eisInv/saveStore")
    RestMessage<String> saveContainerStore(@RequestBody EisInvContainerStoreVo containerStoreVo);

}
