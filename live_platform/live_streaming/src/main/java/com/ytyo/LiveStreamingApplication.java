package com.ytyo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.ytyo.Feign")
public class LiveStreamingApplication {
    public static void main(String[] args) {
        SpringApplication.run(LiveStreamingApplication.class, args);
    }
}
