package com.ytyo.Api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ytyo.Const.GeneralConst;
import com.ytyo.Const.RemoteConst;
import com.ytyo.Feign.LiveAccountFeign;
import com.ytyo.Model.Anchor;
import com.ytyo.Model.User;
import com.ytyo.Option.NoneException;
import com.ytyo.Option.Option;
import com.ytyo.Utils.RequestUtil;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class LiveAccountApi {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    LiveAccountFeign liveAccountFeign;

    public Option<Anchor> getAnchor(HttpServletRequest request) {
        try {
            String userString = request.getHeader(GeneralConst.X_USER_HEADER);
            if (!StringUtils.hasText(userString))
                return Option.None();
            RequestUtil.getUserByReq(request).unwrap();
            Anchor anchor = liveAccountFeign.getAnchor(userString);
            return Option.from(anchor);
        } catch (NoneException | FeignException e) {
            return Option.None();
        }
    }

    public Option<Anchor> getAnchorById(Long anchorId) {
        if (anchorId == null) {
            return Option.None();
        }

        try {
            return Option.from(liveAccountFeign.getAnchorById(anchorId));
        } catch (FeignException e) {
            return Option.None();
        }
//        try {
//
//            RestTemplate restTemplate = new RestTemplate();
//            String url = "http://localhost:8079/getAnchorById?anchorId=" + anchorId;
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.add(RemoteConst.REMOTE_INVOKE_HEADER, RemoteConst.REMOTE_INVOKE_TOKEN);
//            HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(headers);
//
//            ResponseEntity<Anchor> anchor = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Anchor.class);
//
//            if (anchor.getStatusCode() == HttpStatus.OK) {
//                return Option.from(anchor.getBody());
//            } else {
//                System.out.println(anchor.getStatusCode());
//                System.out.println(anchor.getBody());
//                return Option.None();
//            }
//        } catch (RestClientException e) {
//            return Option.None();
//        }
    }


    //feign
    private Anchor remoteInvoke(@RequestHeader(RemoteConst.REMOTE_INVOKE_HEADER) String remoteInvokeToken, @RequestHeader(GeneralConst.X_USER_HEADER) String userString) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add(RemoteConst.REMOTE_INVOKE_HEADER, remoteInvokeToken);
            headers.add(GeneralConst.X_USER_HEADER, userString);

            String url = "http://localhost:8079/getAnchor";

            ResponseEntity<Anchor> anchor = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<String>(headers),
                    Anchor.class
            );

            if (anchor.getStatusCode() == HttpStatus.OK) {
                return anchor.getBody();
            } else {
                return null;
            }
        } catch (RestClientException e) {
            throw new RuntimeException(e);
        }
    }


    public Option<List<User>> getUserByIds(Long... userIds) {
        if (userIds == null)
            return Option.None();
        if (userIds.length == 0) {
            return Option.Some(new ArrayList<>());
        }
        try {
            return Option.from(liveAccountFeign.getUserByIds(List.of(userIds)));
        } catch (FeignException e) {
            return Option.None();
        }
    }


    public Option<User> getUserById(Long userId) {

        try {
            List<User> list = liveAccountFeign.getUserByIds(List.of(userId));
            if (list == null || list.isEmpty()) {
                return Option.None();
            }
            return Option.from(list.get(0));
        } catch (FeignException e) {
            return Option.None();
        }
    }
}
