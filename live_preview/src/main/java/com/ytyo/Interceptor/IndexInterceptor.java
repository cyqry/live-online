package com.ytyo.Interceptor;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class IndexInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getServletPath();
        System.out.println("拦截判断:" + path);
        int len = paths(path);

        String start = getSpilt(path, 0);
        String end = getSpilt(path, len - 1);

        if (
                path.equals("/")
                        || (len == 1 && start.equals("index.html"))
                        ||
                        (
                                (
                                        path.startsWith("/css")
                                                || path.startsWith("/static/css")
                                                || path.startsWith("/static/js")
                                                || path.startsWith("/static/media")
                                )
                                        && end.contains(".")
                        )
                        || (len == 1 && start.equals("asset-manifest.json"))
                        || (len == 1 && start.equals("logo.png"))

        ) {
            return true;
        }

        forward(request, response, "/index.html");
        return false;
    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String path) throws ServletException, IOException {
        request.getRequestDispatcher(path).forward(request, response);
    }

    public static int paths(String path) {
        int length = path.split("/").length;
        return length == 0 ? 0 : length - 1;
    }

    public static String getSpilt(String path, int index) {
        String[] split = path.split("/");
        if (split.length < index + 2) {
            return "";
        } else {
            return split[index + 1];
        }
    }
}
