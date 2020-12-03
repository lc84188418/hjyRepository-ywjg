package com.hjy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan("com.hjy.*.dao")
@SpringBootApplication
@EnableAsync  //开启异步注解功能
@EnableScheduling   //开启基于注解的定时任务
@EnableTransactionManagement//启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
public class YwjgSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(YwjgSystemApplication.class, args);
    }

}
