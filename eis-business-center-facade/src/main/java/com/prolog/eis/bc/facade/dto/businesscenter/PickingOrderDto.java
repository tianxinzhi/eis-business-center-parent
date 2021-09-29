package com.prolog.eis.bc.facade.dto.businesscenter;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class PickingOrderDto {
    @ApiModelProperty("创建时间-开始时间")
    private Date createTimeFrom;

    @ApiModelProperty("创建时间-结束时间")
    private Date createTimeTo;

    @ApiModelProperty("当前页")
    private Integer pageNum;

    @ApiModelProperty("每页行数")
    private Integer pageSize;
    public Date getCreateTimeFrom() {
        return createTimeFrom;
    }

    public void setCreateTimeFrom(Date createTimeFrom) {
        this.createTimeFrom = createTimeFrom;
    }

    public Date getCreateTimeTo() {
        return createTimeTo;
    }

    public void setCreateTimeTo(Date createTimeTo) {
        this.createTimeTo = createTimeTo;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }


}
