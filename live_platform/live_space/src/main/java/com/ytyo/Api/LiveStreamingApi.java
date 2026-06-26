package com.ytyo.Api;


import com.ytyo.Feign.LiveStreamingFeign;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
@Slf4j
public class LiveStreamingApi {
    @Autowired
    LiveStreamingFeign liveStreamingFeign;

    public boolean offlineRoom(Long roomId) {
        if (roomId == null)
            return false;

        try {
            return StringUtils.hasText(liveStreamingFeign.offlineRoom(Map.of("roomId", roomId)));
        } catch (FeignException e) {
            log.error("feign响应错误:", e);
            return false;
        }

//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.set(RemoteConst.REMOTE_INVOKE_HEADER, RemoteConst.REMOTE_INVOKE_TOKEN);
//
//        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
//        map.add("roomId", String.valueOf(roomId));
//
//        HttpEntity<MultiValueMap<String, String>> sendRequest = new HttpEntity<>(map, headers);
//        String url = "http://localhost:8078/offlineRoom";
//        ResponseEntity<String> response = restTemplate.postForEntity(url, sendRequest, String.class);
//        return response.getStatusCode().is2xxSuccessful() && StringUtils.hasText(response.getBody());
    }
}
