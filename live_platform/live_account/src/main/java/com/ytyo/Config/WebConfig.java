package com.ytyo.Config;

import com.ytyo.CommonInterceptor.AuthorityInterceptor;
import com.ytyo.CommonInterceptor.GlobalInterceptor;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    @LoadBalanced//让这个RestTemplate有着自动实现负载均衡的功能;即可识别服务名，并遍历该服务名下的所有实例，然后每次访问这个服务都由其下的实例根据算法实现负载均衡
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }


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
        registry.addInterceptor(authorityInterceptor).addPathPatterns("/**");
    }

}
