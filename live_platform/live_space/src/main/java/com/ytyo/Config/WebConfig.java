package com.ytyo.Config;

import com.ytyo.CommonInterceptor.AuthorityInterceptor;
import com.ytyo.CommonInterceptor.GlobalInterceptor;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    RedisProperties redisProperties;

    @Bean
    public Redisson redisson() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort());

        //看门狗超时时间
//        config.setLockWatchdogTimeout(10000); //设置分布式锁watch dog超时时间

        return (Redisson) Redisson.create(config);
    }

    @Autowired
    GlobalInterceptor globalInterceptor;
    @Autowired
    AuthorityInterceptor authorityInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(globalInterceptor).addPathPatterns("/**");
        registry.addInterceptor(authorityInterceptor).addPathPatterns("/**").excludePathPatterns("/enterRoom", "/leaveRoom", "/getRoomTag", "/getRoomInfo", "/getRoomBase", "/getRoom", "/getRoomPublic");
    }

}