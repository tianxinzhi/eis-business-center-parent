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
import com.prolog.framework.core.exception.PrologException;
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
        RestMessage<List<ContainerLocation>> locationListResp = null;
        try {
            locationListResp = eisContainerRouteClient.findFreeContainerByAreaNo(areaNos);
        } catch (Exception e) {
            log.error("getAllFreeContainerNoByAreaNo({}) excp:{}", areaNos, e.getMessage());
        }
        log.error("getAllFreeContainerNoByAreaNo({}) return:{}", areaNos, JSONObject.toJSONString(locationListResp));
//        List<String> containerNoList = Lists.newArrayList();
        if (null != locationListResp && locationListResp.isSuccess()) {
            // 过滤出有效的容器位置信息
            List<ContainerLocation> locationList = locationListResp.getData();
//            if (!CollectionUtils.isEmpty(locationList)) {
//                for (ContainerLocation cl : locationList) {
//                    if (!StringUtils.isEmpty(cl.getContainerNo())) {
//                        containerNoList.add(cl.getContainerNo());
//                    }
//                }
//            }
            return locationList;
        }
        return Lists.newArrayList();
    }

    /**
     * 获取所有未锁定且索取的站点数据
     * @return
     */
    public List<Station> getAllUnlockAndClaimStation() {
        List<Station> stationList = null;
        RestMessage<List<Station>> stationResp = null;
        try {
            stationResp = eisWarehouseStationFeign.findAllUnlockAndClaimStation();
        } catch (Exception e) {
            log.error("getAllUnlockAndClaimStation excp:{}", e.getMessage());
        }

        log.error("getAllUnlockAndClaimStation return:{}", JSONObject.toJSONString(stationResp));
        if (null != stationResp && stationResp.isSuccess()) {
            stationList = stationResp.getData();
        } else {
            String message = null == stationResp ? "resp is null" : stationResp.getMessage();
            throw new PrologException(message);
        }
        return stationList;
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
        RestMessage<Map<String, StationDto>> areaNoAndContainerCountMapResp = null;
        try {
            areaNoAndContainerCountMapResp = eisContainerRouteClient.findAreaNoAndContainerCountMap(areaNos);
        } catch (Exception e) {
            log.error("findAreaNoAndContainerCountMap({}) excp:{}", areaNos, e.getMessage());
        }
        log.error("findAreaNoAndContainerCountMap({}) return:{}", areaNos, JSONObject.toJSONString(areaNoAndContainerCountMapResp));
        Map<String, StationDto> areaNoAndContainerCountMap = Maps.newHashMap();
        if (null != areaNoAndContainerCountMapResp && areaNoAndContainerCountMapResp.isSuccess()) {
            areaNoAndContainerCountMap = areaNoAndContainerCountMapResp.getData();
        }
        return areaNoAndContainerCountMap;
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
        RestMessage<List<EisInvContainerStoreVo>> invContainerStoreListResp = null;
        try {
            invContainerStoreListResp = eisInvContainerStoreSubFeign.findByContainerNo(containerNos);
        } catch (Exception e) {
            log.error("getInvContainerStoreListByContainerNoList({}) excp:{}", containerNos, e.getMessage());
        }

        log.error("getInvContainerStoreListByContainerNoList({}) return:{}", containerNos, JSONObject.toJSONString(invContainerStoreListResp));

        if (null != invContainerStoreListResp && invContainerStoreListResp.isSuccess()) {
            List<EisInvContainerStoreVo> invContainerStoreList = invContainerStoreListResp.getData();
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
            String message = null == invContainerStoreListResp ? "resp is null" : invContainerStoreListResp.getMessage();
            log.error("getInvContainerStoreListByContainerNoList({}) return error, msg:{}", containerNos, message);
        }
        return Maps.newHashMap();
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
        RestMessage<List<WhLocatorDto>> whLocatorListResp = null;
        try {
            whLocatorListResp = eisWarehouseStationFeign.getWhLocatorListByLocationNos(locationNos);
            log.error("getWhLocatorListByLocationNoList({}) return:{}", locationNos, JSONObject.toJSONString(whLocatorListResp));
        } catch (Exception e) {
            log.error("getWhLocatorListByLocationNoList({}) excp:{}", locationNos, e.getMessage());
        }

        if (null != whLocatorListResp && whLocatorListResp.isSuccess()) {
            List<WhLocatorDto> whLocatorList = whLocatorListResp.getData();
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
            String message = null == whLocatorListResp ? "resp is null" : whLocatorListResp.getMessage();
            log.error("getWhLocatorListByLocationNoList({}) return error, msg:{}", locationNos, message);
        }
        return Maps.newHashMap();
    }
}
