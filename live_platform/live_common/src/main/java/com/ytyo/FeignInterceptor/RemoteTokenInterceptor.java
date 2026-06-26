package com.ytyo.FeignInterceptor;

import com.ytyo.Const.RemoteConst;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class RemoteTokenInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header(RemoteConst.REMOTE_INVOKE_HEADER, RemoteConst.REMOTE_INVOKE_TOKEN);
    }
}
