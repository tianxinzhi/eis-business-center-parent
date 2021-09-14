package com.prolog.eis.bc;

import com.prolog.framework.authority.annotation.EnablePrologResourceServer;
import com.prolog.framework.microservice.annotation.EnablePrologService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/9/13
 **/
@EnableScheduling
@EnablePrologResourceServer
@MapperScan({"com.prolog.eis.bc.dao", "com.prolog.eis.*.dao"})
@EnableAsync
@EnablePrologService
@EnableCaching
public class BusinessApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(BusinessApplication.class, args);
    }
}
