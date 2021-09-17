package com.prolog.eis.bc.facade.dto;

import com.prolog.upcloud.base.inventory.vo.EisInvContainerStoreSubVo;
import lombok.Data;

import java.util.List;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/9/16
 **/
@Data
public class ContainerSelectorDto {

    private String storeId;

    private String containerNo;

    private String areaNo;

    private int x;

    private int y;

    private int distance;

    private float bindNum;

    private int deptNum;

    private List<EisInvContainerStoreSubVo> eisInvContainerStoreSubVoList;
}
