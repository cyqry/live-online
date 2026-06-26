package com.ytyo.Filter;


import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

//@Component

//  WebsocketRoutingFilter 继承了GlobalFilter接口，Order; Order被设置得很大，即默认在很后面才执行这个过滤器
// 但是不知道 WebsocketRoutingFilter 是干嘛的，先不用它

//ws握手的鉴权不知道返回或其它细节。
//干脆制作http的鉴权
public class TestFilter implements GlobalFilter,Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        System.out.println(request.getURI());
        System.out.println(request.getURI().getPath());
        System.out.println(request.getHeaders().getHost().getHostName());
        System.out.println(request.getMethod().name());
        System.out.println(request.getRemoteAddress().getHostName());
        System.out.println(request.getCookies());
        System.out.println(request.getHeaders());

        ServerHttpResponse response = exchange.getResponse();

//        if (request.getURI().getPath().contains("login")) {
//
//            response.getHeaders().set("content-type","application/json,charset=utf-8");
//
//            HashMap<String, String> map = new HashMap<>();
//            map.put("code", "8000");
//            map.put("info", "you can`t");
//
//            ObjectMapper mapper = new ObjectMapper();
//            byte[] bytes = new byte[0];
//            try {
//                bytes = mapper.writeValueAsBytes(map);
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
//            DataBuffer wrap = response.bufferFactory().wrap(bytes);
//            return response.writeWith(Mono.just(wrap));//这里就不放行直接返回了
//
//        } else
//            return chain.filter(exchange);
        return chain.filter(exchange);
    }


    //若有多个过滤器,设置过滤器先后执行
    @Override
    public int getOrder() {
        return -1;//越小越先执行
    }
}

