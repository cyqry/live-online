package com.ytyo.Config;

import com.ytyo.FeignInterceptor.RemoteTokenInterceptor;
import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    // new一个form编码器，实现支持form表单提交
    // 注意这里方法名称，也就是bean的名称是什么不重要，
    // 重要的是返回类型要是 Encoder 并且实现类必须是 FormEncoder 或者其子类

    @Bean
    public Encoder feignFormEncoder() {
        return new FormEncoder(new SpringEncoder(messageConverters));
    }

    @Bean
    public RequestInterceptor requestInterceptor(RemoteTokenInterceptor remoteTokenInterceptor) {
        return remoteTokenInterceptor;
    }
}
