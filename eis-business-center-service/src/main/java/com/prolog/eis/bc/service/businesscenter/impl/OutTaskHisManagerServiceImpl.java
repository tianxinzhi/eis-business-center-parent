package com.prolog.eis.bc.service.businesscenter.impl;

import com.prolog.eis.bc.dao.businesscenter.OutTaskManagerHisDefineMapper;
import com.prolog.eis.bc.facade.dto.businesscenter.OutboundTaskHisDto;
import com.prolog.eis.bc.service.businesscenter.OutTaskHisManagerService;
import com.prolog.framework.core.pojo.Page;
import com.prolog.framework.dao.util.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * OutTaskHisManagerServiceImpl  出库任务单历史管理
 * @author ax
 * @since 2021-09-01
 */
@Service
public class OutTaskHisManagerServiceImpl  implements OutTaskHisManagerService {
    @Autowired
    private OutTaskManagerHisDefineMapper outTaskManagerHisDefineMapper;

    public Page<OutboundTaskHisDto> getOuttaskHisPage(OutboundTaskHisDto dto){
        List<OutboundTaskHisDto> list = outTaskManagerHisDefineMapper.outboundTaskHisDtoPage(dto);
        return  PageUtils.getPage(list);
    }
}
