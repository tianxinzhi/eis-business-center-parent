package com.prolog.eis.bc.service.businesscenter.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.prolog.eis.bc.dao.OutboundTaskReportHisMapper;
import com.prolog.eis.bc.dao.OutboundTaskReportMapper;
import com.prolog.eis.bc.facade.dto.businesscenter.OutboundTaskReportDto;
import com.prolog.eis.bc.service.businesscenter.OutboundTaskReportService;
import com.prolog.eis.core.model.biz.outbound.OutboundTask;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskReport;
import com.prolog.eis.core.model.biz.outbound.OutboundTaskReportHis;
import com.prolog.framework.core.pojo.Page;
import com.prolog.framework.core.restriction.Criteria;
import com.prolog.framework.core.restriction.Restriction;
import com.prolog.framework.core.restriction.Restrictions;
import com.prolog.framework.dao.util.PageUtils;

@Service
public class OutboundTaskReportServiceImpl implements OutboundTaskReportService {
    @Autowired
    private OutboundTaskReportMapper outboundTaskReportMapper;
    @Autowired
    private OutboundTaskReportHisMapper outboundTaskReportHisMapper;

    public Page<OutboundTaskReport> getOutboundTaskReportPage(OutboundTaskReportDto dto){
        PageUtils.startPage(dto.getPageNum(), dto.getPageSize());
        Criteria criteria = new Criteria(OutboundTaskReport.class);
        Restriction r1 = null;
        Restriction r2 = null;
        Restriction r3 = null;
        Restriction r4 = null;
        Restriction r5 = null;
        Restriction r6 = null;
        Restriction r7 = null;
        if (!StringUtils.isEmpty(dto.getOutTaskId())) {
            r1 = Restrictions.like("outTaskId", "%" + dto.getOutTaskId() + "%");
        }
        if (!StringUtils.isEmpty(dto.getUpperSystemTaskId())) {
            r2 = Restrictions.like("upperSystemTaskId", "%" + dto.getUpperSystemTaskId() + "%");
        }
        if (!StringUtils.isEmpty(dto.getOutboundTaskTypeNo())) {
            r3 = Restrictions.eq("outboundTaskTypeNo", dto.getOutboundTaskTypeNo());
        }
        if (dto.getCreateTimeFrom() != null) {
            r4 = Restrictions.ge("createTime", dto.getCreateTimeFrom());
        }
        if (dto.getCreateTimeTo() != null) {
            r5 = Restrictions.le("createTime", dto.getCreateTimeTo());
        }
        if (dto.getReportTimeFrom() != null) {
            r6 = Restrictions.ge("reportTime", dto.getReportTimeFrom());
        }
        if (dto.getReportTimeTo() != null) {
            r7 = Restrictions.le("reportTime", dto.getReportTimeTo());
        }
        criteria.setRestriction(Restrictions.and(r1,r2,r3,r4,r5,r6,r7));
        List<OutboundTaskReport> list = outboundTaskReportMapper.findByCriteria(criteria);
        return PageUtils.getPage(list);
    }

    @Override
    public void batchConvertAndInsert(List<OutboundTask> outboundTaskList)
            throws Exception {
        if (CollectionUtils.isEmpty(outboundTaskList)) {
            return;
        }
        List<OutboundTaskReport> insertObjList = Lists.newArrayList();
        for (OutboundTask outboundTask : outboundTaskList) {
            OutboundTaskReport insertObj = new OutboundTaskReport();
            insertObj.setOutTaskId(outboundTask.getId());
            insertObj.setOutboundTaskTypeNo(outboundTask.getOutboundTaskTypeNo());
            insertObj.setUpperSystemTaskId(outboundTask.getUpperSystemTaskId());
            insertObj.setCreateTime(new Date());
            insertObjList.add(insertObj);
        }
        outboundTaskReportMapper.saveBatch(insertObjList);
    }

    @Override
    public List<OutboundTaskReport> findAll() {
        Criteria criteria = new Criteria(OutboundTaskReport.class);
        return outboundTaskReportMapper.findByCriteria(criteria);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void toCallbackHis(OutboundTaskReport dto) throws Exception {
        if (StringUtils.isEmpty(dto.getId())) {
            throw new Exception("任务ID不能为空！");
        }
        OutboundTaskReport one = outboundTaskReportMapper.findById(dto.getId(), OutboundTaskReport.class);
        if (null == one) {
            throw new Exception("出库任务回告ID不存在！ id=" + dto.getId());
        }
        OutboundTaskReportHis insertHis = new OutboundTaskReportHis();
        // 复制数据 回告->回告历史
        BeanUtils.copyProperties(one, insertHis);
        // 入库回告历史
        outboundTaskReportHisMapper.save(insertHis);
        // 删除回告
        outboundTaskReportMapper.deleteById(dto.getId(), OutboundTaskReport.class);
    }

}
