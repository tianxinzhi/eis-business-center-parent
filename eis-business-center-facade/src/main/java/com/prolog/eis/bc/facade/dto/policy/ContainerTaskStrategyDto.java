package com.prolog.eis.bc.facade.dto.policy;

import com.prolog.framework.core.annotation.AutoKey;
import com.prolog.framework.core.annotation.Column;
import com.prolog.framework.core.annotation.Id;
import com.prolog.framework.core.annotation.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 容器任务单策略(ctrl_eis_container_task_stg)实体类
 *
 * @author: hzw
 * @create: 2021-09-27
 * @Version: V1.0
 */
@Data
public class ContainerTaskStrategyDto {

    @Id
    @Column("id")
    @ApiModelProperty("id")
    @AutoKey(type = AutoKey.TYPE_SNOWFLAKE)
    private String id;

    @ApiModelProperty("容器任务单类型编号")
    private String containerTaskTypeNo;

    @ApiModelProperty("容器任务单类型名称")
    private String typeName;

    @ApiModelProperty("容器任务单类型编号")
    private int priority;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("企业id")
    private String enterpriseId;

    @ApiModelProperty("货主id")
    private String cargoOwnerId;

    @ApiModelProperty("仓库id")
    private String warehouseId;
    
    @ApiModelProperty("开始时间")
    private String startTime;

    @ApiModelProperty("结束时间")
    private String endTime;
    
    @ApiModelProperty("当前页")
    private String pageNum;

    @ApiModelProperty("每页行数")
    private String pageSize;
    
    @ApiModelProperty("区域集合")
    private List<String> areaList;
}
