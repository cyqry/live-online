package com.ytyo.Filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ytyo.Manager.RedisManager;
import com.ytyo.Model.User;
import com.ytyo.Option.NoneException;
import com.ytyo.Option.Option;
import com.ytyo.Utils.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.ytyo.Const.GeneralConst.*;
import static com.ytyo.Const.GateWayConst.*;

@Component
@Slf4j
public class AuthenticationFilter implements GlobalFilter, Ordered {

    // 注入你的 Redis 服务类实例
    @Autowired
    private RedisManager redisManager;

    @Autowired
    private ObjectMapper mapper;

    private final List<String> whiteList = List.of(
            "/signIn",              // 登录接口
            "/signUp",           // 注册接口
            "^/public(/.*)?$",
            "/static/user/image",
            "/static/system/image",
            "^/direct/static(/.*)?$"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        //添加网关信息,并将后方需要的Header初始化
        ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate()
                .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET_VALUE)
                .header(X_USER_HEADER, "");
        try {

            // 检查请求路径是否在白名单中
            boolean isWhiteListed = whiteList.stream().anyMatch(path::matches);
            // 如果在白名单中，直接放行
            if (isWhiteListed) {
                return chain.filter(exchange.mutate().request(requestBuilder.build()).build());
            }
            Option<User> user = verifyAndGetUser(exchange.getRequest().getCookies());
            if (user.isNone() && !path.startsWith("/webrtc_sub")) {
                return unauthorized(exchange);
            }
            // 如果验证通过，继续处理后续请求
            if (user.isSome()) {
                requestBuilder
                        .header(X_USER_HEADER, RequestUtil.encodeUser(user.unwrap()).unwrap());
            }
            //未设置请求头却通过的是游客的ws连接
            return chain.filter(exchange.mutate().request(requestBuilder.build()).build());
        } catch (NoneException e) {
            return unauthorized(exchange);
        }
    }

    Option<User> verifyAndGetUser(MultiValueMap<String, HttpCookie> cookies) {
        List<HttpCookie> liveSession = cookies.get("live_session");
        if (liveSession == null || liveSession.size() < 1) {
            return Option.None();
        } else {
            String cookieValue = liveSession.get(0).getValue();
            if (!StringUtils.hasText(cookieValue)) {
                return Option.None();
            } else {
                return redisManager.getUserByCookie(cookieValue);
            }
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        // 设置响应状态码和内容类型
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        // 构建错误信息
//        String body = "{\"message\": \"" + message + "\"}";
        // 返回错误信息
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap("未登录".getBytes())));
    }

    @Override
    public int getOrder() {
        //RewritePath 的优先级应该是是10000
        return 20000;
    }
}
