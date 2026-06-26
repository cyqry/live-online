package com.ytyo;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.List;

@MapperScan({"com.ytyo.Model", "com.ytyo.Dao"})
@SpringBootApplication
@EnableFeignClients(basePackages = "com.ytyo.Feign")
public class LiveAccountApplication {
    public static void main(String[] args) {
        SpringApplication.run(LiveAccountApplication.class, args);
        System.out.println("Completed");
    }
}
