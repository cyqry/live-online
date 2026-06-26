package com.ytyo.CommonInterceptor;

import com.ytyo.Utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.io.PrintWriter;

@Component
//CorsWebFilter是网关才会有的，这个类在网关中不加载
@ConditionalOnMissingClass("com.ytyo.Filter.CorsWebFilter")
public class GlobalInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!RequestUtil.verifyGateway(request) && !RequestUtil.verifyRemote(request) && !RequestUtil.verifyAdminServer(request))
            return FalseResponse(response);
        return true;
    }

    private boolean FalseResponse(HttpServletResponse response) {
        try {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // 创建一个错误信息
            String errorMessage = "illegal access";
            PrintWriter writer = response.getWriter();
            writer.write(errorMessage); // 将错误信息写入响应体
            writer.flush();
        } catch (IOException ignore) {
        }
        return false;
    }
}
