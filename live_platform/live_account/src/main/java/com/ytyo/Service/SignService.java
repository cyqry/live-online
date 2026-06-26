package com.ytyo.Service;

import com.ytyo.Dao.UserMapper;
import com.ytyo.Manager.UserManager;
import com.ytyo.Model.User;
import com.ytyo.Option.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.HashMap;

import static com.ytyo.Utils.SignUtil.makeCookieValue;

@Service
@Validated
public class SignService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserManager userManager;

    public boolean signUp(User user) {
        if (user == null)
            return false;
        HashMap<String, Object> conditions = new HashMap<>();
        conditions.put("email", user.getEmail());
        conditions.put("phone", user.getPhone());
        if (userMapper.countByMap(conditions) > 0) {
            return false;
        }
        int i = userMapper.insertUser(user);
        return i >= 1;
    }

    public boolean logout(String cookieValue) {
        if (cookieValue == null)
            return false;
        return userManager.deleteUser(cookieValue);
    }

    public Option<ResponseCookie> signIn(String account, String password, int remember) {
        if (!StringUtils.hasText(password) || !StringUtils.hasText(account)) {
            return Option.None();
        }

        User user = userMapper.getUserByPhonePassword(account, password);
        if (user == null) {
            user = userMapper.getUserByEmailPassword(account, password);
        }

        if (user != null) {
            int days = remember == 1 ? 7 : 1;
            String cookieValue = makeCookieValue(account, password);
            boolean saved = userManager.saveCookieUser(cookieValue, user, days);
            if (saved) {
                return Option.Some(
                        ResponseCookie.from("live_session", cookieValue)
                                .sameSite("none")
                                .secure(true)
                                .httpOnly(true)
                                .path("/")
                                //浏览器保存的时间长一点
                                .maxAge(Duration.ofDays(days + 1))
                                .build());
            } else {
                return Option.None();
            }
        } else {
            return Option.None();
        }

    }
}
