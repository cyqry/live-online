package com.ytyo.Controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TesController {
    public List<HttpSession> sessions = new ArrayList<>();

    //    前端设置with-credentials:true，后端即使不设置CORS头也可set-cookie成功
//        前端不设置with-credentials或者false，set-cookie响应头会被直接忽略
    @RequestMapping("/setCookie")
    public ResponseEntity<?> setCookie(HttpServletRequest request, HttpServletResponse response) {
//        if (sessions.contains(request.getSession())){
//            System.out.println("存在!");
//        }
//        else {
//            System.out.println("不存在1");
//            sessions.add(request.getSession());
//        }
//        System.out.println(sessions.size());


        response.setContentType("text/html;charset=utf-8");
        //获取当前时间
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        LocalDate date = LocalDate.now(ZoneId.systemDefault());
        //取得cookie
        Cookie[] cookies = request.getCookies();
        String lastTime = null;

        //第n次访问
        if (cookies != null) {
            System.out.println("第n次访问");
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("lastTime")) {
                    //有lastTime的cookie，已经是第n次访问
                    lastTime = cookie.getValue();//上次访问的时间
                    //第n次访问
                    //1.把上次显示时间显示到浏览器
//                    response.getWriter().write("欢迎回来，你上次访问的时间为：" + lastTime + ",当前时间为：" + date);
                    //2.更新cookie
                    cookie.setValue(date.toString());
                    cookie.setMaxAge(30 * 24 * 60 * 60);
                    //3.把更新后的cookie发送到浏览器
                    response.addCookie(cookie);
                    break;
                }
            }
        }

        /**
         * 第一次访问（没有cookie 或 有cookie，但没有名为lastTime的cookie）
         */
        if (cookies == null || lastTime == null) {
            System.out.println("第1次访问");
            //1.显示当前时间到浏览器
//            response.getWriter().write("你是首次访问本网站，当前时间为：" + date);
            //2.创建Cookie对象

            ResponseCookie cookie = ResponseCookie.from("lasttime")
                    .httpOnly(true)        // 禁止js读取cookie
                    .secure(true)        // false为在http下也传输,true的话只有https请求浏览器才会携带cookie
                    .domain("localhost")// 域名
                    .path("/")            // path
                    .maxAge(Duration.ofHours(1))    // 1个小时候过期
                    .sameSite("none")//sameSite 为Strict的话，设置浏览器只有页面访问同源网站才会携带cookie，edge默认：为Lax就没有此限制
                    .build();
            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }
        return new ResponseEntity<>("set", HttpStatus.OK);
    }
}
