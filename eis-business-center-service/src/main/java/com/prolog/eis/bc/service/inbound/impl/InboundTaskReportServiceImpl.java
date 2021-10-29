package com.prolog.eis.bc.service.inbound.impl;

import com.prolog.eis.bc.dao.inbound.InboundTaskReportHisMapper;
import com.prolog.eis.bc.dao.inbound.InboundTaskReportMapper;
import com.prolog.eis.bc.facade.dto.inbound.InboundTaskReportDto;
import com.prolog.eis.bc.facade.dto.inbound.InboundTaskReportHisDto;
import com.prolog.eis.bc.facade.vo.inbound.InboundTaskReportHisVo;
import com.prolog.eis.bc.facade.vo.inbound.InboundTaskReportVo;
import com.prolog.eis.bc.service.inbound.InboundTaskReportService;
import com.prolog.eis.core.model.base.test.LocationCheckDetail;
import com.prolog.eis.core.model.biz.inbound.InboundTask;
import com.prolog.eis.core.model.biz.inbound.InboundTaskReport;
import com.prolog.eis.core.model.biz.inbound.InboundTaskReportHis;
import com.prolog.framework.core.pojo.Page;
import com.prolog.framework.core.restriction.Criteria;
import com.prolog.framework.core.restriction.FieldSelector;
import com.prolog.framework.core.restriction.Restrictions;
import com.prolog.framework.dao.util.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @author: wuxl
 * @create: 2021-10-20 15:04
 * @Version: V1.0
 */
@Service
@Slf4j
public class InboundTaskReportServiceImpl implements InboundTaskReportService {
    @Autowired
    private InboundTaskReportMapper inboundTaskReportMapper;
    @Autowired
    private InboundTaskReportHisMapper inboundTaskReportHisMapper;

    @Override
    public Page<InboundTaskReportVo> listInboundTaskReportByPage(InboundTaskReportDto dto) {
        PageUtils.startPage(dto.getPageNum(), dto.getPageSize());
        List<InboundTaskReportVo> inboundTaskReportVoList = inboundTaskReportMapper.findByParam(dto);
        return PageUtils.getPage(inboundTaskReportVoList);
    }

    @Override
    public Page<InboundTaskReportHisVo> listInboundTaskReportHisByPage(InboundTaskReportHisDto dto) {
        PageUtils.startPage(dto.getPageNum(), dto.getPageSize());
        List<InboundTaskReportHisVo> inboundTaskReportHisVoList = inboundTaskReportHisMapper.findByParam(dto);
        return PageUtils.getPage(inboundTaskReportHisVoList);
    }

    @Override
    public void toReport(InboundTask inboundTask, String location) {
        InboundTaskReport taskReport = new InboundTaskReport();
        taskReport.setInboundTaskId(inboundTask.getId());
        taskReport.setUpperSystemTaskId(inboundTask.getUpperSystemTaskId());
        taskReport.setCreateTime(new Date());
        taskReport.setExtend1(location);

        inboundTaskReportMapper.save(taskReport);
    }

    @Override
    public List<InboundTaskReport> findAll() {
        Criteria criteria = new Criteria(InboundTaskReport.class);
        return inboundTaskReportMapper.findByCriteria(criteria);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void toCallbackHis(InboundTaskReport dto) throws Exception {
        if (StringUtils.isEmpty(dto.getId())) {
            throw new Exception("入库任务回告ID不能为空！");
        }
        InboundTaskReport one = inboundTaskReportMapper.findById(dto.getId(), InboundTaskReport.class);
        if (null == one) {
            throw new Exception("入库任务回告ID不存在！ id=" + dto.getId());
        }
        InboundTaskReportHis insertHis = new InboundTaskReportHis();
        // 复制数据 回告->回告历史
        BeanUtils.copyProperties(one, insertHis);
        // 入库回告历史
        inboundTaskReportHisMapper.save(insertHis);
        // 删除回告
        inboundTaskReportMapper.deleteById(dto.getId(), InboundTaskReport.class);
    }

    @Override
    public void toCallbackFail(InboundTaskReport dto) throws Exception {
        if (StringUtils.isEmpty(dto.getId())) {
            throw new Exception("入库任务回告ID不能为空！");
        }
        InboundTaskReport updateObj = new InboundTaskReport();
        updateObj.setErrorMsg(dto.getErrorMsg());
        FieldSelector field = FieldSelector.newInstance().include(new String[] { "errorMsg" });
        Criteria updateCrt = new Criteria(LocationCheckDetail.class);
        updateCrt.setRestriction(Restrictions.eq("id", dto.getId()));
        long effectNum = inboundTaskReportMapper.updateFieldsByCriteria(updateObj, field, updateCrt);
        log.error("toCallbackFail({},{}) return:{}", dto.getId(), dto.getErrorMsg(), effectNum);
        if (effectNum != 1L) {
            throw new Exception("更新入库回告失败！");
        }
    }

}
