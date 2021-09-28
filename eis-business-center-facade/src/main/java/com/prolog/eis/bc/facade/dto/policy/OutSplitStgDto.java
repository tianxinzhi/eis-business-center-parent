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
public class OutSplitStgDto {
	@Id
    @AutoKey(type = AutoKey.TYPE_SNOWFLAKE)
    private String id;

    @Column("strategy_name")
    @ApiModelProperty("策略名称")
    private String strategyName;

    @Column("strategy_type_no")
    @ApiModelProperty("策略类型编码")
    private String strategyTypeNo;

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
