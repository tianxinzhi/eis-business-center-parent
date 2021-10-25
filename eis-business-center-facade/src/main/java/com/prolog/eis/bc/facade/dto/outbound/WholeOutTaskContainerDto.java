package com.prolog.eis.bc.facade.dto.outbound;

import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.InvStockAlgorithmDto;
import com.prolog.upcloud.base.strategy.dto.eis.outbound.whole.OutTaskAlgorithmDto;

import java.util.List;

public class WholeOutTaskContainerDto {

    /**
     * 站台集合
     */
    private List<WholeStationDto> wholeStationDtoList;

    /**
     * 待执行的出库订单
     */
    private List<OutTaskAlgorithmDto> outTaskAlgorithmDtoList;

    /**
     * 库存
     */
    private List<InvStockAlgorithmDto> invStockAlgorithmDtoList;

    public List<WholeStationDto> getWholeStationDtoList() {
        return wholeStationDtoList;
    }

    public void setWholeStationDtoList(List<WholeStationDto> wholeStationDtoList) {
        this.wholeStationDtoList = wholeStationDtoList;
    }

    public List<OutTaskAlgorithmDto> getOutTaskAlgorithmDtoList() {
        return outTaskAlgorithmDtoList;
    }

    public void setOutTaskAlgorithmDtoList(
            List<OutTaskAlgorithmDto> outTaskAlgorithmDtoList) {
        this.outTaskAlgorithmDtoList = outTaskAlgorithmDtoList;
    }

    public List<InvStockAlgorithmDto> getInvStockAlgorithmDtoList() {
        return invStockAlgorithmDtoList;
    }

    public void setInvStockAlgorithmDtoList(
            List<InvStockAlgorithmDto> invStockAlgorithmDtoList) {
        this.invStockAlgorithmDtoList = invStockAlgorithmDtoList;
    }

}
