package com.prolog.eis.bc.service.osr;

import com.prolog.eis.bc.facade.dto.osr.OutSummaryOrderInfoDto;
import com.prolog.eis.bc.facade.dto.osr.SplitStrategyResultDto;
import com.prolog.upcloud.base.inventory.vo.EisInvContainerStoreSubVo;
import com.prolog.upcloud.base.inventory.vo.EisInvContainerStoreVo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: txz
 * @Date: 2021/9/26 12:13
 * @Desc:
 */
public class SplitStrategy {

    private static  List<List<Double>> subQtys = new LinkedList<>();
    private static  List<List<String>> subContainNos = new LinkedList<>();

    /**
     * 整托
     * @param orderQty 订单
     * @param vos 可用的容器
     * @return
     */
    public static SplitStrategyResultDto zhengTuoStrategy(double orderQty, List<EisInvContainerStoreVo> vos){
        SplitStrategyResultDto reSplit = new SplitStrategyResultDto();


        for (int i = vos.size() - 1; i >= 0; i--) {
            EisInvContainerStoreVo container = vos.get(i);

            double totalSubQty = 0;
            List<String> subNos = new LinkedList<>();//子容器号
            List<Double> subQtys = new LinkedList<>();
            for (int i1 = container.getContainerStoreSubList().size() - 1; i1 >= 0; i1--) {
                EisInvContainerStoreSubVo subCon = container.getContainerStoreSubList().get(i1);
                totalSubQty += subCon.getQty();
                subNos.add(subCon.getContainerStoreSubNo());
                subQtys.add(subCon.getQty());
            }
            //找到合适的容器，记录子容器，并移除对应容器
            if(totalSubQty >= orderQty){
                reSplit.setContainerNos(Arrays.asList(container.getContainerNo()));
                //reSplit.setOrderTotalQty(totalSubQty-orderTotalQty);
                reSplit.setSubContainerNos(Arrays.asList(subNos));
                reSplit.setSubConQtys(Arrays.asList(subQtys));

                vos.remove(container);
                break;
            }
        }

        return reSplit;
    }

    /**
     * 零发整
     * @param orderQty 订单
     * @param vos 可用容器
     * @return
     */
    public static SplitStrategyResultDto pinXiangStrategy(double orderQty, List<EisInvContainerStoreVo> vos){
        List<String> containNos = new LinkedList<>();
        SplitStrategyResultDto strategy = new SplitStrategyResultDto();
        strategy.setRemainOrderQty(orderQty);
        for (int i = vos.size() - 1; i >= 0; i--) {
            if(strategy.getRemainOrderQty() != null && strategy.getRemainOrderQty().doubleValue() <= 0) {
                break;
            }
            EisInvContainerStoreVo container = vos.get(i);
            //子容器按数量排序
            List<EisInvContainerStoreSubVo> subCons = container.getContainerStoreSubList().stream().sorted(Comparator.comparing(EisInvContainerStoreSubVo::getQty)).collect(Collectors.toList());
            LinkedList<Double> subQty = new LinkedList<>();
            LinkedList<String> subConNo = new LinkedList<>();


            strategy = findZs(strategy,subQty,subConNo,  subCons);
            containNos.add(container.getContainerNo());

        }
        strategy.setContainerNos(containNos);
        strategy.setSubContainerNos(subContainNos);
        strategy.setSubConQtys(subQtys);
        return strategy;
    }


    /**
     * 递归查找合适的子容器
     * @param sus
     * @param sQs
     * @param sNos
     * @param subCons
     * @return
     */
    public static SplitStrategyResultDto findZs(SplitStrategyResultDto sus, List<Double> sQs, List<String> sNos, List<EisInvContainerStoreSubVo> subCons){
        boolean isFind = false;
        //找子容器列表，找到则结束递归循环
        Iterator<EisInvContainerStoreSubVo> iterator = subCons.iterator();
        while (iterator.hasNext()) {
            EisInvContainerStoreSubVo subContainer = iterator.next();
            //找到则添加进结果集
            if(sus.getRemainOrderQty() >0 && subContainer.getQty() >= sus.getRemainOrderQty()){
                sus.setContainerOverQty(subContainer.getQty() - sus.getRemainOrderQty());

                sQs.add(sus.getRemainOrderQty());
                sus.setRemainOrderQty(0d);

                sNos.add(subContainer.getContainerStoreSubNo());
                isFind = true;
                iterator.remove();

                subQtys.add(sQs);
                subContainNos.add(sNos);
                break;
            }
        }
        //找不到合适的容器
        if(subCons.size() != 0 && !isFind) {
            //找列表末尾最大的一个加进结果集，然后用剩余的orderQty继续递归查找
            EisInvContainerStoreSubVo maxSubCon = subCons.get(subCons.size() - 1);
            double maxQty = maxSubCon.getQty();
            sQs.add(maxQty);
            sNos.add(maxSubCon.getContainerStoreSubNo());
            iterator.remove();
            sus.setRemainOrderQty(sus.getRemainOrderQty() - maxQty);
            //递归
            findZs(sus,sQs,sNos,subCons);
        } else if(isFind == false && subCons.size() == 0){
            //子容器用完都找不到合适的
            subQtys.add(sQs);
            subContainNos.add(sNos);
        }
        return sus;
    }

//    public static void main(String[] args) {
//        OutSummaryOrderInfoDto.OutSummaryOrderDetailInfoDto dtl = new OutSummaryOrderInfoDto.OutSummaryOrderDetailInfoDto();
//        dtl.setOrderQty(1500d);
//        OutSummaryOrderInfoDto order = new OutSummaryOrderInfoDto("",null,null,null,null,null,null);
//        order.setDtls(Arrays.asList(dtl));
//
//        List<EisInvContainerStoreVo> vos = new LinkedList<>();
//        EisInvContainerStoreVo vo = new EisInvContainerStoreVo();
//        vo.setContainerNo("001");
//
//        EisInvContainerStoreSubVo subC1 = new EisInvContainerStoreSubVo();
//        subC1.setQty(20);
//        subC1.setContainerStoreSubNo("001_001");
//
//        EisInvContainerStoreSubVo subC2 = new EisInvContainerStoreSubVo();
//        subC2.setQty(30);
//        subC2.setContainerStoreSubNo("001_002");
//        vo.setContainerStoreSubList(Arrays.asList(subC1,subC2));
//
//        EisInvContainerStoreVo vo2 = new EisInvContainerStoreVo();
//        vo2.setContainerNo("002");
//        EisInvContainerStoreSubVo subC3 = new EisInvContainerStoreSubVo();
//        subC3.setQty(50);
//        subC3.setContainerStoreSubNo("002_001");
//        EisInvContainerStoreSubVo subC4 = new EisInvContainerStoreSubVo();
//        subC4.setQty(60);
//        subC4.setContainerStoreSubNo("002_002");
//        vo2.setContainerStoreSubList(Arrays.asList(subC3,subC4));
//
//        vos.add(vo);
//        vos.add(vo2);
//
////        for (EisInvContainerStoreVo containerStoreVo : vos) {
////            System.out.println(containerStoreVo);
////        }
////
////        boolean remove = vos.remove(vos.get(vos.size() - 1));
////
////        System.out.println("isRemove: "+remove);
////        for (EisInvContainerStoreVo containerStoreVo : vos) {
////            System.out.println(containerStoreVo);
////        }
//
//        SplitStrategyResultDto splitStrategyParamDtos = pinXiangStrategy(170, vos);
//        System.out.println(splitStrategyParamDtos);
//    }

}
