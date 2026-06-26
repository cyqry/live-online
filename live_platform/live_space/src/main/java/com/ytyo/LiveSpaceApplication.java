package com.ytyo;

import com.ytyo.Option.NoneException;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@MapperScan({"com.ytyo.Model", "com.ytyo.Dao"})
@EnableFeignClients(basePackages = "com.ytyo.Feign")
public class LiveSpaceApplication {
    public static void main(String[] args) throws NoneException {
        ConfigurableApplicationContext context = SpringApplication.run(LiveSpaceApplication.class, args);
        System.out.println("Completed");
    }
}
