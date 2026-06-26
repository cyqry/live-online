package com.ytyo.Controller;
import com.ytyo.Model.User;
import jakarta.validation.constraints.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@Slf4j
//为了单个参数校验所加
@Validated
public class ValidController {


    @PostMapping("/test1")
    //     不支持表单提交,即contentType为application/x-www-form-urlencoded的post请求,会直接报错，支持application/json的请求
    public String test1(@Validated @RequestBody User user){
        log.info("test1: 用户是 is {}", user);
        return "test1 valid is success";
    }

    //单个参数,由于无@Validated,所以需要在类上加一个@Validated
    @PostMapping(value = "/test3")
    public String test3(@Email String email){
        log.info("email is {}", email);
//        log.info("n is {}", n);
//        log.info("user is {}", user);
        return "email valid success";
    }
}