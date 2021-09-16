package com.prolog.eis.bc.feign;

import com.prolog.eis.bc.feign.product.GoodsInfoInterfaceFeign;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: xiaozhi
 * @Date: 2021/9/14 14:49
 * @Desc:
 */
@Slf4j
@Component
public class DefaultFallBackFactory implements FallbackFactory<GoodsInfoInterfaceFeign> {

    @Autowired
    private HttpServletRequest request;

    @Override
    public GoodsInfoInterfaceFeign create(Throwable throwable) {
        String url = request.getRequestURL().toString();

        //打印请求参数
        log.info("服务异常: " + url);
        log.info(throwable.getCause().toString());
        return null;
    }
}
