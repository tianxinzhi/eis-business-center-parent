package com.prolog.eis.bc.service.outboundtask.impl;

import com.prolog.eis.bc.dao.OutboundTaskDetailMapper;
import com.prolog.eis.bc.service.outboundtask.OutboundTaskDetailService;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskDetail;
import com.prolog.framework.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @Author: txz
 * @Date: 2021/10/13 16:24
 * @Desc:
 */
@Service
public class OutboundTaskDetailServiceImpl implements OutboundTaskDetailService {

    @Autowired
    private OutboundTaskDetailMapper mapper;

    @Override
    public List<OutboundTaskDetail> getByOutTaskId(String taskId) {
        Assert.notNull(taskId,"任务单id不能为空");
        List<OutboundTaskDetail> outTaskId = mapper.findByMap(MapUtils.put("outTaskId", taskId).getMap(), OutboundTaskDetail.class);
        return outTaskId;
    }

}
