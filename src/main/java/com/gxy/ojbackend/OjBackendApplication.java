package com.gxy.ojbackend;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.gxy.ojbackend.mapper")
@EnableScheduling
@Slf4j
@SpringBootApplication
public class OjBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(OjBackendApplication.class, args);
        log.info("启动成功");
    }

}
