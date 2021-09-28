package com.prolog.eis.bc.facade.dto.policy;

import java.util.List;
import java.util.Map;

import com.prolog.framework.core.annotation.AutoKey;
import com.prolog.framework.core.annotation.Column;
import com.prolog.framework.core.annotation.Id;
import com.prolog.framework.core.annotation.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 出库任务单策略配置(ctrl_eis_out_stg_cfg)实体类
 *
 * @author: hzw
 * @create: 2021-09-27
 * @Version: V1.0
 */
@Data
public class OutStgDto {
    @Id
    @AutoKey(type = AutoKey.TYPE_SNOWFLAKE)
    private String id;

    @Column("type_no")
    @ApiModelProperty("出库任务单类型编号")
    private String typeNo;

    @Column("type_name")
    @ApiModelProperty("出库任务单类型名称")
    private String typeName;

    @Column("out_model")
    @ApiModelProperty("出库模式(批拣单出库、订单池出库)")
    private String outModel;

    @Column("dispatch_priority")
    @ApiModelProperty("调度优先级(调度执行顺序)")
    private int dispatchPriority;

    @Column("compose_order_config")
    @ApiModelProperty("组单策略配置")
    private String composeOrderConfig;

    @Column("max_order_num")
    @ApiModelProperty("最大组单数量")
    private int maxOrderNum;

    @Column("max_order_volume")
    @ApiModelProperty("最大组单体积")
    private int maxOrderVolume;

    @Column("store_matching_strategy")
    @ApiModelProperty("库存匹配策略(1.按品种、2.按品批)")
    private int storeMatchingStrategy;

    @Column("outbound_expiry_date_rate")
    @ApiModelProperty("优先出库最高时效百分比")
    private int outboundExpiryDateRate;

    @Column("prohibit_expiry_date_rate")
    @ApiModelProperty("禁止出库最高时效百分比")
    private int prohibitExpiryDateRate;

    @Column("clear_store_strategy")
    @ApiModelProperty("清库存策略(0.否 1.是)")
    private int clearStoreStrategy;

    @Column("enterprise_id")
    @ApiModelProperty("企业id")
    private String enterpriseId;

    @Column("cargo_owner_id")
    @ApiModelProperty("货主id")
    private String cargoOwnerId;

    @Column("warehouse_id")
    @ApiModelProperty("仓库id")
    private String warehouseId;
    
    @ApiModelProperty("当前页")
    private String pageNum;

    @ApiModelProperty("每页行数")
    private String pageSize;

    @ApiModelProperty("区域集合")
    private List<Map> areaList;
}
