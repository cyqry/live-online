package com.ytyo.Service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthenticationService {
    public boolean verifyPassword(String oldPassword, String password) {
        if (StringUtils.hasText(password) && StringUtils.hasText(password)) {
            return oldPassword.equals(password);
        }
        return false;
    }

    public boolean verifyIdentity(String idNumber, String realName) {
        return true;
    }

}
