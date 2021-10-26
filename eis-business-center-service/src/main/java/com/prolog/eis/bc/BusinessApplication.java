package com.prolog.eis.bc;

import com.prolog.framework.authority.annotation.EnablePrologEmptySecurityServer;
import com.prolog.framework.authority.annotation.EnablePrologResourceServer;
import com.prolog.framework.bz.common.search.EnableSearchApi;
import com.prolog.framework.microservice.annotation.EnablePrologService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/9/13
 **/
@EnableScheduling
@EnablePrologEmptySecurityServer
//@EnablePrologResourceServer
@MapperScan({"com.prolog.eis.bc.dao", "com.prolog.eis.*.dao"})
@EnableAsync
@EnablePrologService
@EnableCaching
@EnableTransactionManagement
@EnableSearchApi
public class BusinessApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(BusinessApplication.class, args);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
