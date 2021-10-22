package com.prolog.eis.bc.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.prolog.eis.bc.feign.container.EisContainerRouteClient;
import com.prolog.eis.common.util.MathHelper;
import com.prolog.eis.core.model.biz.route.ContainerLocation;
import com.prolog.framework.common.message.RestMessage;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FeignService {

    @Autowired
    private EisContainerRouteClient eisContainerRouteClient;

    /**
     * 获取区域中所有空闲的容器
     * @param areaNoList 区域集合
     * @return
     */
    public List<String> getAllFreeContainerNoByAreaNo(List<String> areaNoList) {
        if (CollectionUtils.isEmpty(areaNoList)) {
            return Lists.newArrayList();
        }
        String areaNos = MathHelper.strListToStr(areaNoList, ",");
        // 查询货位是否存在容器
        RestMessage<List<ContainerLocation>> locationMapContainerResp = null;
        try {
            locationMapContainerResp = eisContainerRouteClient.findFreeContainerByAreaNo(areaNos);
        } catch (Exception e) {
            log.error("eisContainerRouteClient.findFreeContainerByAreaNo({}) excp:{}", areaNos, e.getMessage());
        }
        log.error("eisContainerRouteClient.findFreeContainerByAreaNo({}) return:{}", areaNos, JSONObject.toJSONString(locationMapContainerResp));
        List<String> containerNoList = Lists.newArrayList();
        if (null != locationMapContainerResp && locationMapContainerResp.isSuccess()) {
            // 过滤出有效的容器位置信息
            List<ContainerLocation> locationList = locationMapContainerResp.getData();
            if (!CollectionUtils.isEmpty(locationList)) {
                for (ContainerLocation cl : locationList) {
                    if (!StringUtils.isEmpty(cl.getContainerNo())) {
                        containerNoList.add(cl.getContainerNo());
                    }
                }
            }
        }
        return containerNoList;
    }
}
