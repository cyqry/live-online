package com.ytyo.Api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ytyo.Const.GeneralConst;
import com.ytyo.Const.RemoteConst;
import com.ytyo.Feign.LiveAccountFeign;
import com.ytyo.Model.Anchor;
import com.ytyo.Option.NoneException;
import com.ytyo.Option.Option;
import com.ytyo.Utils.RequestUtil;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Slf4j
public class LiveAccountApi {

    @Autowired
    LiveAccountFeign liveAccountFeign;
    @Autowired
    ObjectMapper objectMapper;

    //这里这个入参和其他getAnchor不一样
    public Option<Anchor> getAnchor(ServerHttpRequest request) {
        if (request == null)
            return Option.None();
        List<String> list = request.getHeaders().get(GeneralConst.X_USER_HEADER);
        if (list == null || list.isEmpty()) {
            return Option.None();
        }
        try {
            String userString = list.get(0);
            if (!StringUtils.hasText(userString))
                return Option.None();
            RequestUtil.getUserByReq(request).unwrap();
            Anchor anchor = liveAccountFeign.getAnchor(userString);
            return Option.from(anchor);
        } catch (NoneException | FeignException e) {
            return Option.None();
        }
    }

    public Option<Boolean> isAnchor(ServerHttpRequest request) {
        if (request == null)
            return Option.None();
        List<String> list = request.getHeaders().get(GeneralConst.X_USER_HEADER);
        if (list == null || list.isEmpty()) {
            return Option.None();
        }
        try {
            String userString = list.get(0);
            if (!StringUtils.hasText(userString))
                return Option.None();
            RequestUtil.getUserByReq(request).unwrap();
            return Option.from(liveAccountFeign.isAnchor(userString));
        } catch (NoneException | FeignException e) {
            return Option.None();
        }
    }

    //feign
    private Anchor remoteInvoke(@RequestHeader(RemoteConst.REMOTE_INVOKE_HEADER) String remoteInvokeToken, @RequestHeader(GeneralConst.X_USER_HEADER) String userString) {
        try {
            //添加房间
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add(RemoteConst.REMOTE_INVOKE_HEADER, remoteInvokeToken);
            headers.add(GeneralConst.X_USER_HEADER, userString);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

            String url = "http://localhost:8079/getAnchor";

            ResponseEntity<Anchor> anchor = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<String>(headers),
                    Anchor.class
            );

            if (anchor.getStatusCode() == HttpStatus.OK) {
                System.out.println(anchor.getBody());
                return anchor.getBody();
            } else {
                System.out.println(anchor.getStatusCode());
                System.out.println(anchor.getBody());
                // handle error
                return null;
            }
        } catch (RestClientException e) {
            throw new RuntimeException(e);
        }
    }
}
