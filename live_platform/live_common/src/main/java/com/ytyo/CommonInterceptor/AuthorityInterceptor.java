package com.ytyo.CommonInterceptor;

import com.ytyo.annotation.authority.Remote;
import com.ytyo.annotation.authority.RemoteNoLoginRequired;
import com.ytyo.annotation.authority.SuperAdmin;
import com.ytyo.Model.User;
import com.ytyo.Option.NoneException;
import com.ytyo.Utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.io.PrintWriter;


@Component
@Slf4j
//CorsWebFilter是网关才会有的，这个类在网关中不加载
@ConditionalOnMissingClass("com.ytyo.Filter.CorsWebFilter")
public class AuthorityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (RequestUtil.verifyAdminServer(request) || request.getRequestURI().startsWith("/public")) {
            return true;
        }


        //是Http请求，非静态资源等请求
        if (handler instanceof HandlerMethod method) {
            Class<?> declaringClass = method.getMethod().getDeclaringClass();
            RemoteNoLoginRequired declaringClassRemoteNoLoginRequired = declaringClass.getAnnotation(RemoteNoLoginRequired.class);
            Remote declaringClassRemote = declaringClass.getAnnotation(Remote.class);
            SuperAdmin declaringClassSuperAdmin = declaringClass.getAnnotation(SuperAdmin.class);

            RemoteNoLoginRequired methodRemoteNoLoginRequired = method.getMethodAnnotation(RemoteNoLoginRequired.class);
            Remote methodRemote = method.getMethodAnnotation(Remote.class);
            SuperAdmin methodSuperAdmin = method.getMethodAnnotation(SuperAdmin.class);

            try {
                boolean isRemote = RequestUtil.verifyRemote(request);

                //是远程调用,才校验登录，非远程调用在网关层就校验是否登录了
                if (isRemote) {
                    if (declaringClassRemoteNoLoginRequired == null && methodRemoteNoLoginRequired == null) {
                        RequestUtil.getUserByReq(request).unwrap();
                    }
                }

                //只要需要超级管理员权限，就校验
                if (declaringClassSuperAdmin != null || methodSuperAdmin != null) {
                    User user = RequestUtil.getUserByReq(request).unwrap();
                    if (!isSuperAdmin(user))
                        return FalseResponse(response);
                }
                //必须是远程调用
                if (methodRemote != null || declaringClassRemote != null) {
                    if (!isRemote)
                        return FalseResponse(response);
                }
            } catch (NoneException e) {
                return FalseResponse(response);
            }
        } else {
            return FalseResponse(response);
        }
        return true;
    }

    private boolean FalseResponse(HttpServletResponse response) {
        try {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // 创建一个错误信息
            String errorMessage = "Authentication failed";
            PrintWriter writer = response.getWriter();
            writer.write(errorMessage); // 将错误信息写入响应体
            writer.flush();
        } catch (IOException ignore) {
        }
        return false;
    }


    private boolean isSuperAdmin(User user) {
        return user != null && user.getRole() != null && user.getRole() >= 2;
    }
}