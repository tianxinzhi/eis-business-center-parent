package com.prolog.eis.bc.service.outboundtask;

import com.prolog.eis.bc.facade.vo.OutboundStrategyConfigVo;
import com.prolog.eis.core.model.ctrl.outbound.OutboundStrategyConfig;
import com.prolog.framework.core.exception.PrologException;

import java.util.List;

/**
 * @Author clarence_she
 * @Date 2021/9/15
 **/
public interface OutboundStrategyConfigService {

    /**
     * 查询出库任务策略
     * @return
     * @throws Exception
     */
    OutboundStrategyConfigVo findConfigByTypeNo(String typeNo)throws PrologException;

    /**
     * 获取指定出库模式策略的出库策略集合
     * @param outModel
     * @return
     * @throws PrologException
     */
    List<OutboundStrategyConfig> getByOutModel(String outModel) throws PrologException;

    /**
     * 获取指定出库类型的出库策略集合
     * @param outType 出库类型
     * @return
     * @throws PrologException
     */
    List<OutboundStrategyConfigVo> getByOutType(int outType) throws PrologException;

    /**
     * 查询所有策略
     * @return
     */
    List<OutboundStrategyConfigVo> findAll();
}
