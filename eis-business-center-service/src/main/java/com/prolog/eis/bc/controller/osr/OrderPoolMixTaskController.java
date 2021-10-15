package com.prolog.eis.bc.controller.osr;

import com.prolog.eis.bc.service.osr.OrderPoolMixTaskService;
import com.prolog.framework.common.message.RestMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: txz
 * @Date: 2021/10/14 15:32
 * @Desc:
 */
@RestController
@RequestMapping("api/v1/orderPool")
public class OrderPoolMixTaskController {

    @Autowired
    private OrderPoolMixTaskService service;

    @RequestMapping("/mixToSummaryOrder")
    public RestMessage hd(){
        try {
            String s = service.geneOrderPoolToOutSummaryOrder();
            return RestMessage.newInstance(true,"ok",s);
        } catch (Exception e) {
            e.printStackTrace();
            return RestMessage.error("汇单失败");
        }
    }
}
