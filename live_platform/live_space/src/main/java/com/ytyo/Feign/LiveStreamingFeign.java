package com.ytyo.Feign;

import com.ytyo.Config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@FeignClient(value = "LiveStreaming", configuration = FeignConfig.class)
public interface LiveStreamingFeign {
    @PostMapping(value = "/offlineRoom", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String offlineRoom(Map<String, ?> formParamsMap);
}
