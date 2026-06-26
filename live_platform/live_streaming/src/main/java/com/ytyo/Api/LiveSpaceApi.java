package com.ytyo.Api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ytyo.Feign.LiveSpaceFeign;
import com.ytyo.Model.RoomDetails;
import com.ytyo.Model.User;
import com.ytyo.Option.NoneException;
import com.ytyo.Option.Option;
import com.ytyo.Utils.RequestUtil;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
public class LiveSpaceApi {

    @Test
    public void test() {
        getRoom(23);
    }

    @Autowired
    LiveSpaceFeign liveSpaceFeign;

    @Autowired
    ObjectMapper objectMapper;


    public Option<RoomDetails> getRoom(long roomId) {

        try {
            return Option.from(liveSpaceFeign.getRoom(roomId));
        } catch (FeignException e) {
            return Option.None();
        }

//        try {
//            //添加房间
//            RestTemplate restTemplate = new RestTemplate();
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//            headers.add(RemoteConst.REMOTE_INVOKE_HEADER, remoteInvokeToken);
//
//
//            String url = "http://localhost:8081/getRoom?roomId=" + roomId;
//
//            ResponseEntity<RoomDetails> anchor = restTemplate.exchange(
//                    url,
//                    HttpMethod.GET,
//                    new HttpEntity<String>(headers),
//                    RoomDetails.class
//            );
//
//            if (anchor.getStatusCode() == HttpStatus.OK) {
//                System.out.println(anchor.getBody());
//                return Option.from(anchor.getBody());
//            } else {
//                System.out.println(anchor.getStatusCode());
//                System.out.println(anchor.getBody());
//                // handle error
//                return Option.None();
//            }
//        } catch (RestClientException e) {
//            return Option.None();
//        }
    }

    public boolean updateLastLiveTime(Long roomId, User user) {
        if (roomId == null || user == null)
            return false;

        try {
            return StringUtils.hasText(liveSpaceFeign.updateLastLiveTime(Map.of("roomId", roomId), RequestUtil.encodeUser(user).unwrap()));
        } catch (NoneException | FeignException e) {
            return false;
        }

//        RestTemplate restTemplate = new RestTemplate();
//
//
//        String url = "http://localhost:8081/updateLastLiveTime";
//        try {
//            // 请求头设置,x-www-form-urlencoded格式的数据
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//            headers.add(RemoteConst.REMOTE_INVOKE_HEADER, remoteInvokeToken);
//            headers.add(CommonConst.X_USER_HEADER, objectMapper.writeValueAsString(user));
//
//            //提交参数设置
//            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
//            map.add("roomId", String.valueOf(roomId));
//            // 组装请求体
//            HttpEntity<MultiValueMap<String, String>> request =
//                    new HttpEntity<>(map, headers);
//
//            String result = restTemplate.postForObject(url, request, String.class);
//            System.out.println(result);
//            return StringUtils.hasText(result);
//        } catch (RestClientException | JsonProcessingException e) {
//            e.printStackTrace();
//            return false;
//        }
    }


    public boolean EnterRoom(Long roomId, Short who, Long id, Option<User> user) {
        if (roomId == null || user == null || who == null || id == null) {
            return false;
        }

        try {
            String userString = "";
            if (user.isSome()) {
                userString = RequestUtil.encodeUser(user.unwrap()).unwrap();
            }
            String result = liveSpaceFeign.enterRoom(Map.of("roomId", roomId, "who", who, "id", id), userString);
            return StringUtils.hasText(result);
        } catch (NoneException | FeignException e) {
            return false;
        }

//        RestTemplate restTemplate = new RestTemplate();
//
//
//        String url = "http://localhost:8081/enterRoom";
//        try {
//            // 请求头设置,x-www-form-urlencoded格式的数据
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//            headers.add(RemoteConst.REMOTE_INVOKE_HEADER, remoteInvokeToken);
        //                          这里 未使用urlencode 编码，所以错误
//            if (user.isSome())
//                headers.add(CommonConst.X_USER_HEADER, objectMapper.writeValueAsString(user.unwrap()));
//
//            //提交参数设置
//            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
//            map.add("roomId", String.valueOf(roomId));
//            map.add("who", String.valueOf(who));
//            map.add("id", String.valueOf(id));
//            // 组装请求体
//            HttpEntity<MultiValueMap<String, String>> request =
//                    new HttpEntity<>(map, headers);
//
//            String result = restTemplate.postForObject(url, request, String.class);
//            System.out.println(result);
//            return StringUtils.hasText(result);
//        } catch (RestClientException | JsonProcessingException e) {
//            e.printStackTrace();
//            return false;
//        } catch (NoneException e) {
//            log.error("不会出现");
//            throw new RuntimeException(e);
//        }

    }


    public boolean LeaveRoom(Long roomId, Short who, Long id, Option<User> user) {
        if (roomId == null || user == null || who == null || id == null) {
            return false;
        }

        try {
            String userString = "";
            if (user.isSome()) {
                userString = RequestUtil.encodeUser(user.unwrap()).unwrap();
            }
            String result = liveSpaceFeign.leaveRoom(Map.of("roomId", roomId, "who", who, "id", id), userString);
            return StringUtils.hasText(result);
        } catch (NoneException | FeignException e) {
            return false;
        }


//        RestTemplate restTemplate = new RestTemplate();
//        String url = "http://localhost:8081/leaveRoom";
//        try {
//            // 请求头设置,x-www-form-urlencoded格式的数据
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//            headers.add(RemoteConst.REMOTE_INVOKE_HEADER, remoteInvokeToken);
//            //                          这里 未使用urlencode 编码，所以错误
//            if (user.isSome())
//                headers.add(CommonConst.X_USER_HEADER, objectMapper.writeValueAsString(user.unwrap()));
//
//
//            //提交参数设置
//            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
//            map.add("roomId", String.valueOf(roomId));
//            map.add("who", String.valueOf(who));
//            map.add("id", String.valueOf(id));
//            // 组装请求体
//            HttpEntity<MultiValueMap<String, String>> request =
//                    new HttpEntity<>(map, headers);
//
//            String result = restTemplate.postForObject(url, request, String.class);
//            System.out.println(result);
//            return StringUtils.hasText(result);
//        } catch (RestClientException | JsonProcessingException e) {
//            e.printStackTrace();
//            return false;
//        } catch (NoneException e) {
//            log.error("不会出现");
//            throw new RuntimeException(e);
//        }
    }

    public boolean offLineRoom(Long roomId, User user) {
        //user是为了过登录校验
        if (roomId == null || user == null) {
            return false;
        }

        try {
            String result = liveSpaceFeign.offlineRoom(Map.of("roomId", roomId), RequestUtil.encodeUser(user).unwrap());
            return StringUtils.hasText(result);
        } catch (NoneException | FeignException e) {
            return false;
        }

//        String remoteInvokeToken = RemoteConst.REMOTE_INVOKE_TOKEN;
//
//
//        RestTemplate restTemplate = new RestTemplate();
//        String url = "http://localhost:8081/ofLineRoom";
//        try {
//            // 请求头设置,x-www-form-urlencoded格式的数据
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//            headers.add(RemoteConst.REMOTE_INVOKE_HEADER, remoteInvokeToken);
//            headers.add(CommonConst.X_USER_HEADER, objectMapper.writeValueAsString(user));
//
//
//            //提交参数设置
//            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
//            map.add("roomId", String.valueOf(roomId));
//            // 组装请求体
//            HttpEntity<MultiValueMap<String, String>> request =
//                    new HttpEntity<>(map, headers);
//
//            String result = restTemplate.postForObject(url, request, String.class);
//            System.out.println(result);
//            return StringUtils.hasText(result);
//        } catch (RestClientException | JsonProcessingException e) {
//            e.printStackTrace();
//            return false;
//        }
    }
}
