package com.ytyo;


import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAdminServer
public class LiveAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(LiveAdminApplication.class, args);

    }
}
