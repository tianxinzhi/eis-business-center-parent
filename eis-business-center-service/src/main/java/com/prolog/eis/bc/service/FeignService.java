package com.prolog.eis.bc.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.prolog.eis.bc.feign.EisInvContainerStoreSubFeign;
import com.prolog.eis.bc.feign.EisWarehouseStationFeign;
import com.prolog.eis.bc.feign.container.EisContainerRouteClient;
import com.prolog.eis.common.util.MathHelper;
import com.prolog.eis.component.algorithm.composeorder.entity.StationDto;
import com.prolog.eis.core.dto.route.WhLocatorDto;
import com.prolog.eis.core.model.base.area.Station;
import com.prolog.eis.core.model.biz.route.ContainerLocation;
import com.prolog.framework.common.message.RestMessage;
import com.prolog.upcloud.base.inventory.vo.EisInvContainerStoreVo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FeignService {

    @Autowired
    private EisContainerRouteClient eisContainerRouteClient;

    @Autowired
    private EisWarehouseStationFeign eisWarehouseStationFeign;

    @Autowired
    private EisInvContainerStoreSubFeign eisInvContainerStoreSubFeign;

    /**
     * 获取区域中所有空闲的容器
     * @param areaNoList 区域集合
     * @return
     */
    public List<ContainerLocation> getAllFreeContainerNoByAreaNo(List<String> areaNoList) {
        if (CollectionUtils.isEmpty(areaNoList)) {
            return Lists.newArrayList();
        }
        String areaNos = MathHelper.strListToStr(areaNoList, ",");
        // 查询空闲容器
        RestMessage<List<ContainerLocation>> webResp = null;
        try {
            webResp = eisContainerRouteClient.findFreeContainerByAreaNo(areaNos);
        } catch (Exception e) {
            log.error("getAllFreeContainerNoByAreaNo({}) excp:{}", areaNos, e.getMessage());
            throw new RuntimeException(e);
        }
        log.info("getAllFreeContainerNoByAreaNo({}) return:{}", areaNos, JSONObject.toJSONString(webResp));
        if (null != webResp && webResp.isSuccess()) {
            return webResp.getData();
        } else {
            String message = null == webResp ? "resp is null" : webResp.getMessage();
            log.error("eisContainerRouteClient.findFreeContainerByAreaNo({}) return error, msg:{}", areaNos, message);
            throw new RuntimeException(message);
        }
    }

    /**
     * 获取所有未锁定且索取的站点数据
     * @return
     */
    public List<Station> getAllUnlockAndClaimStation() {
        RestMessage<List<Station>> webResp = null;
        try {
            webResp = eisWarehouseStationFeign.findAllUnlockAndClaimStation();
        } catch (Exception e) {
            log.error("getAllUnlockAndClaimStation excp:{}", e.getMessage());
            throw new RuntimeException(e);
        }

        log.info("getAllUnlockAndClaimStation return:{}", JSONObject.toJSONString(webResp));
        if (null != webResp && webResp.isSuccess()) {
            return webResp.getData();
        } else {
            String message = null == webResp ? "resp is null" : webResp.getMessage();
            log.error("eisWarehouseStationFeign.findAllUnlockAndClaimStation() return error, msg:{}", message);
            throw new RuntimeException(message);
        }
    }

    /**
     * 根据区域集合查询对应区域的托盘数量信息
     * @param areaNoList 区域集合
     * @return
     */
    public Map<String, StationDto> findAreaNoAndContainerCountMap(List<String> areaNoList) {
        if (CollectionUtils.isEmpty(areaNoList)) {
            return Maps.newHashMap();
        }
        String areaNos = MathHelper.strListToStr(areaNoList, ",");
        // 查询空闲容器
        RestMessage<Map<String, StationDto>> webResp = null;
        try {
            webResp = eisContainerRouteClient.findAreaNoAndContainerCountMap(areaNos);
        } catch (Exception e) {
            log.error("findAreaNoAndContainerCountMap({}) excp:{}", areaNos, e.getMessage());
            throw new RuntimeException(e);
        }
        log.info("findAreaNoAndContainerCountMap({}) return:{}", areaNos, JSONObject.toJSONString(webResp));
        if (null != webResp && webResp.isSuccess()) {
            return webResp.getData();
        } else {
            String message = null == webResp ? "resp is null" : webResp.getMessage();
            log.error("eisContainerRouteClient.findAreaNoAndContainerCountMap({}) return error, msg:{}", areaNos, message);
            throw new RuntimeException(message);
        }
    }

    /**
     * 获取托盘的库存信息
     * @param containerNoList 库存信息
     * @return
     */
    public Map<String, EisInvContainerStoreVo> getInvContainerStoreListByContainerNoList(
            List<String> containerNoList) {
        if (CollectionUtils.isEmpty(containerNoList)) {
            return Maps.newHashMap();
        }
        String containerNos = MathHelper.strListToStr(containerNoList, ",");
        // 根据托盘号查询托盘库存信息
        RestMessage<List<EisInvContainerStoreVo>> webResp = null;
        try {
            webResp = eisInvContainerStoreSubFeign.findByContainerNo(containerNos);
        } catch (Exception e) {
            log.error("getInvContainerStoreListByContainerNoList({}) excp:{}", containerNos, e.getMessage());
            throw new RuntimeException(e);
        }

        log.info("getInvContainerStoreListByContainerNoList({}) return:{}", containerNos, JSONObject.toJSONString(webResp));

        if (null != webResp && webResp.isSuccess()) {
            List<EisInvContainerStoreVo> invContainerStoreList = webResp.getData();
            if (CollectionUtils.isEmpty(invContainerStoreList)) {
                return Maps.newHashMap();
            }
            Map<String, List<EisInvContainerStoreVo>> result = invContainerStoreList.stream().filter(e -> null != e.getContainerNo()).collect(Collectors.groupingBy(e -> e.getContainerNo()));
            Map<String, EisInvContainerStoreVo> containerNoAndStoreMap = Maps.newHashMap();
            for (String containerNo : result.keySet()) {
                if (!CollectionUtils.isEmpty(result.get(containerNo))) {
                    containerNoAndStoreMap.put(containerNo, result.get(containerNo).get(0));
                }
            }
            return containerNoAndStoreMap;
        } else {
            String message = null == webResp ? "resp is null" : webResp.getMessage();
            log.error("eisInvContainerStoreSubFeign.findByContainerNo({}) return error, msg:{}", containerNos, message);
            throw new RuntimeException(message);
        }
    }

    /**
     * 根据LocationNo获取位置信息详情
     * @param locationNoList locationNo集合
     * @return
     */
    public Map<String, WhLocatorDto> getWhLocatorListByLocationNoList(
            List<String> locationNoList) {
        if (CollectionUtils.isEmpty(locationNoList)) {
            return Maps.newHashMap();
        }
        String locationNos = MathHelper.strListToStr(locationNoList, ",");
        // 根据托盘号查询托盘库存信息
        RestMessage<List<WhLocatorDto>> webResp = null;
        try {
            webResp = eisWarehouseStationFeign.getWhLocatorListByLocationNos(locationNos);
            log.info("getWhLocatorListByLocationNoList({}) return:{}", locationNos, JSONObject.toJSONString(webResp));
        } catch (Exception e) {
            log.error("getWhLocatorListByLocationNoList({}) excp:{}", locationNos, e.getMessage());
            throw new RuntimeException(e);
        }

        if (null != webResp && webResp.isSuccess()) {
            List<WhLocatorDto> whLocatorList = webResp.getData();
            if (CollectionUtils.isEmpty(whLocatorList)) {
                return Maps.newHashMap();
            }
            Map<String, List<WhLocatorDto>> result = whLocatorList.stream().filter(e -> null != e.getLocationNo()).collect(Collectors.groupingBy(e -> e.getLocationNo()));
            Map<String, WhLocatorDto> locationNoAndLocatorMap = Maps.newHashMap();
            for (String locationNo : result.keySet()) {
                if (!CollectionUtils.isEmpty(result.get(locationNo))) {
                    locationNoAndLocatorMap.put(locationNo, result.get(locationNo).get(0));
                }
            }
            return locationNoAndLocatorMap;
        } else {
            String message = null == webResp ? "resp is null" : webResp.getMessage();
            log.error("getWhLocatorListByLocationNoList({}) return error, msg:{}", locationNos, message);
            throw new RuntimeException(message);
        }
    }

}
