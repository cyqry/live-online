package com.ytyo.Utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.StringUtils;

public class SignUtil {
    public static String makeCookieValue(String... params) {
        StringBuilder cookieValue = new StringBuilder();
        for (String param : params) {
            cookieValue.append(param);
        }
        return DigestUtils.sha256Hex(cookieValue.toString());
    }
}
