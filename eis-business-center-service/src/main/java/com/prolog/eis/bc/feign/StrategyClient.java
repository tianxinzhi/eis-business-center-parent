package com.prolog.eis.bc.feign;

import com.prolog.framework.common.message.RestMessage;
import com.prolog.upcloud.base.strategy.dto.StrategyDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("${prolog.service.strategy:upcloud-base-strategy}")
public interface StrategyClient {

    /**
     * 查询执行策略
     *
     * @param enterpriseId 企业id
     * @param category     策略分类[上架：InStock,下架:OutStock,补货:Replenishment]
     * @param cargoOwnerId 货主id
     * @param warehouseId  仓库id
     * @return 执行策略
     */
    @GetMapping("/strategy/v1/execute")
    RestMessage<StrategyDTO> getStrategyDTO(@RequestParam("enterpriseId") String enterpriseId,
                                            @RequestParam("category") String category,
                                            @RequestParam("cargoOwnerId") String cargoOwnerId,
                                            @RequestParam("warehouseId") String warehouseId);

    /**
     * 查询执行策略
     *
     * @param enterpriseId  企业id
     * @param category      策略分类[上架：InStock,下架:OutStock,补货:Replenishment]
     * @param cargoOwnerIds 货主id集合
     * @param warehouseId   仓库id
     * @return 执行策略
     */
    @GetMapping("/strategy/v1/execute/batch")
    RestMessage<List<StrategyDTO>> getStrategyDTO(@RequestParam("enterpriseId") String enterpriseId,
                                                  @RequestParam("category") String category,
                                                  @RequestParam("cargoOwnerIds") String[] cargoOwnerIds,
                                                  @RequestParam("warehouseId") String warehouseId);

}
