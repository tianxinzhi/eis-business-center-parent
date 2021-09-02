package com.prolog.eis.sc.dto.supply;

import lombok.Data;

/**
 * @author: wuxl
 * @create: 2021-08-19 10:24
 * @Version: V1.0
 */
@Data
public class ContainerStoreDto {


    private int id;
    /**
     * 容器号
     */
    private String containerNo;

    /**
     * 容器类型 -1空托剁 0非整托 1整托
     */
    private int containerType;

    /**
     * 任务类型 0无业务任务 10非整托入库 11整托入库 12移库入库 20非整托出库 21整托出库 22演示出库 25空托出库
     */
    private int taskType;

    /**
     * 区域
     */
    private String areaNo;
}
