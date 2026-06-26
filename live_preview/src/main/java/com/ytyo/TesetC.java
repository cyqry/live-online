package com.ytyo;

import com.ytyo.Interceptor.IndexInterceptor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TesetC {
    @GetMapping("/t")
    public String test(){
        return "6666asdfasfasfasfasfa6666";
    }

    public static void main(String[] args) {
        String s = "/static/static/js/main.49eacee2.js";
        System.out.println(IndexInterceptor.paths(s));
        System.out.println(IndexInterceptor.getSpilt(s,0));
        System.out.println(IndexInterceptor.getSpilt(s,1));
    }
}


