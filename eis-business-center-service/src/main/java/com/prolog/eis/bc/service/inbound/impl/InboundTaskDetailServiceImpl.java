package com.prolog.eis.bc.service.inbound.impl;

import com.prolog.eis.bc.dao.inbound.InboundTaskDetailHisMapper;
import com.prolog.eis.bc.dao.inbound.InboundTaskDetailMapper;
import com.prolog.eis.bc.facade.vo.inbound.InboundTaskDetailHisVo;
import com.prolog.eis.bc.facade.vo.inbound.InboundTaskDetailVo;
import com.prolog.eis.bc.service.inbound.InboundTaskDetailService;
import com.prolog.eis.bc.service.inbound.InboundTaskDetailSubService;
import com.prolog.eis.common.util.ListHelper;
import com.prolog.eis.core.model.biz.inbound.InboundTaskDetail;
import com.prolog.eis.core.model.biz.inbound.InboundTaskDetailHis;
import com.prolog.eis.core.model.biz.inbound.InboundTaskDetailSub;
import com.prolog.eis.core.model.biz.inbound.InboundTaskDetailSubHis;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: wuxl
 * @create: 2021-10-18 17:55
 * @Version: V1.0
 */
@Service
@Slf4j
public class InboundTaskDetailServiceImpl implements InboundTaskDetailService {
    @Autowired
    private InboundTaskDetailMapper inboundTaskDetailMapper;
    @Autowired
    private InboundTaskDetailHisMapper inboundTaskDetailHisMapper;
    @Autowired
    private InboundTaskDetailSubService inboundTaskDetailSubService;

    @Override
    public List<InboundTaskDetailVo> listInboundTaskDetailByParam(String inboundTaskId) {
        List<InboundTaskDetailVo> inboundTaskDetailVoList = inboundTaskDetailMapper.findByParam(inboundTaskId);
        List<InboundTaskDetailSub> inboundTaskDetailSubList = inboundTaskDetailSubService.listInboundTaskDetailSubByParam(null);
        inboundTaskDetailVoList.forEach(vo -> {
            List<InboundTaskDetailSub> where = ListHelper.where(inboundTaskDetailSubList, s -> vo.getId().equals(s.getInboundTaskDetailId()));
            vo.setSubSize(where.size());
            vo.setInboundTaskDetailSubList(where);
        });
        return inboundTaskDetailVoList;
    }

    @Override
    public List<InboundTaskDetailHisVo> listInboundTaskDetailHisByParam(String inboundTaskId) {
        List<InboundTaskDetailHisVo> inboundTaskDetailHisVoList = inboundTaskDetailHisMapper.findByParam(inboundTaskId);
        List<InboundTaskDetailSubHis> inboundTaskDetailSubHisList = inboundTaskDetailSubService.listInboundTaskDetailSubHisByParam(null);
        inboundTaskDetailHisVoList.forEach(vo -> {
            List<InboundTaskDetailSubHis> where = ListHelper.where(inboundTaskDetailSubHisList, s -> vo.getId().equals(s.getInboundTaskDetailId()));
            vo.setSubSize(where.size());
            vo.setInboundTaskDetailSubHisList(where);
        });
        return inboundTaskDetailHisVoList;
    }

    @Override
    public void toHistory(InboundTaskDetail inboundTaskDetail) throws Exception {
        if (StringUtils.isEmpty(inboundTaskDetail.getId())) {
            throw new Exception("ID不能为空！");
        }
        InboundTaskDetailHis his = new InboundTaskDetailHis();
        BeanUtils.copyProperties(inboundTaskDetail, his);
        inboundTaskDetailHisMapper.save(his);
        inboundTaskDetailMapper.deleteById(inboundTaskDetail.getId(), InboundTaskDetail.class);
    }

    @Override
    public long saveBatch(List<InboundTaskDetail> detailList) {
        return inboundTaskDetailMapper.saveBatch(detailList);
    }

    @Override
    public long save(InboundTaskDetail detail) {
        return inboundTaskDetailMapper.save(detail);
    }
}
