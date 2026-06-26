package com.ytyo.Controller;

import com.ytyo.Model.User;
import com.ytyo.Option.NoneException;
import com.ytyo.Option.Option;
import com.ytyo.Service.SignService;
import com.ytyo.Utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
public class SignController {
    @Autowired
    SignService signService;

    private boolean hasSignUpRequiredInfo(User user) {
        return StringUtils.hasText(user.getNickname()) && StringUtils.hasText(user.getGender()) && StringUtils.hasText(user.getPhone()) && StringUtils.hasText(user.getPassword());
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@Validated @RequestBody User user) {
        //todo todo todo
//        if (true){
//            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("暂时不行");
//        }
        if (!hasSignUpRequiredInfo(user)){
            return ResponseEntity.badRequest().body("参数错误");
        }
        user.setProperty(null);
        user.setRealName(null);
        user.setIdNumber(null);
        user.setBalance(null);
        user.setRole(null);
        user.setAvatar(null);
        boolean signed = signService.signUp(user);
        if (signed) {
            return ResponseEntity.ok("注册成功");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("用户的邮箱或手机已绑定账号");
        }
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(String account, String password, int remember, String code, HttpServletResponse response) throws NoneException {
        Option<ResponseCookie> cookie = signService.signIn(account, password, remember);
        log.info("Signing in");
        if (cookie.isSome()) {
            response.setHeader(HttpHeaders.SET_COOKIE, cookie.unwrap().toString());
            return new ResponseEntity<>("登录成功!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("账号或密码错误", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) throws NoneException {
        String cookieValue = RequestUtil.getCookieValueByReq(request).unwrap();
        if (!signService.logout(cookieValue)) {
            log.error("没有这个用户!");
        }
        response.setHeader(HttpHeaders.SET_COOKIE,
                ResponseCookie.from("live_session", "")
                        .sameSite("none")
                        .secure(true)
                        .httpOnly(true)
                        .path("/")
                        .build().toString());
        return ResponseEntity.ok("登出成功!");
    }
}
