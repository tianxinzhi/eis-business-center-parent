package com.prolog.eis.bc;

import com.prolog.framework.authority.annotation.EnablePrologEmptySecurityServer;
import com.prolog.framework.bz.common.search.EnableSearchApi;
import com.prolog.framework.microservice.annotation.EnablePrologService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/9/13
 **/
@EnableScheduling
//@EnablePrologResourceServer
@EnablePrologEmptySecurityServer
@MapperScan({"com.prolog.eis.bc.dao", "com.prolog.eis.*.dao"})
@EnableAsync
@EnablePrologService
@EnableCaching
@EnableTransactionManagement
@EnableSearchApi
public class BusinessApplication {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(60000);
        httpRequestFactory.setConnectTimeout(60000);
        httpRequestFactory.setReadTimeout(60000);
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // 支持中文编码
        return restTemplate;
    }

    public static void main(String[] args) { SpringApplication.run(BusinessApplication.class, args); }
}
