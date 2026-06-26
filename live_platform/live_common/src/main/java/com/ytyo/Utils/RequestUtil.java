package com.ytyo.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.ytyo.Const.GateWayConst;
import com.ytyo.Const.GeneralConst;
import com.ytyo.Const.RemoteConst;
import com.ytyo.Model.User;
import com.ytyo.Option.Option;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RequestUtil {
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule())
                .registerModule(new ParameterNamesModule());
    }

    public static Option<String> getCookieValueByReq(HttpServletRequest request) {
        if (request.getCookies() == null)
            return Option.None();

        for (Cookie cookie : request.getCookies()) {
            if ("live_session".equals(cookie.getName())) {
                return Option.from(cookie.getValue());
            }
        }
        return Option.None();
    }

    public static Option<User> getUserByReq(HttpServletRequest request) {
        if (request == null)
            return Option.None();
        String header = request.getHeader(GeneralConst.X_USER_HEADER);
        return decodeUser(header);
    }

    public static Option<User> getUserByReq(ServerHttpRequest request) {
        if (request == null)
            return Option.None();
        List<String> list = request.getHeaders().get(GeneralConst.X_USER_HEADER);
        if (list == null || list.isEmpty()) {
            return Option.None();
        }
        String header = list.get(0);
        return decodeUser(header);
    }

    public static Option<String> encodeUser(User user) {
        if (user == null) {
            return Option.None();
        }
        try {
            String userStr = objectMapper.writeValueAsString(user);
            return Option.Some(URLEncoder.encode(userStr, StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            return Option.None();
        }
    }

    public static Option<User> decodeUser(String userHeader) {
        if (userHeader == null) {
            return Option.None();
        }
        try {
            User user = objectMapper.readValue(URLDecoder.decode(userHeader, StandardCharsets.UTF_8), User.class);
            return Option.from(user);
        } catch (JsonProcessingException e) {
            return Option.None();
        }
    }


    public static boolean verifyRemote(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        String token = request.getHeader(RemoteConst.REMOTE_INVOKE_HEADER);
        return RemoteConst.REMOTE_INVOKE_TOKEN.equals(token);
    }

    public static boolean verifyGateway(HttpServletRequest request) {
        ;
        if (request == null) {
            return false;
        }
        String token = request.getHeader(GateWayConst.GATEWAY_SECRET_HEADER);
        return GateWayConst.GATEWAY_SECRET_VALUE.equals(token);
    }

    public static boolean verifyAdminServer(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        String token = request.getHeader(GeneralConst.X_ADMIN_SERVER_HEADER);
        return GeneralConst.X_ADMIN_SERVER_TOKEN.equals(token);
    }
}
