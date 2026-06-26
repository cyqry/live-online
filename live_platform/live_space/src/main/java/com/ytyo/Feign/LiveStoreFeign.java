package com.ytyo.Feign;

import com.ytyo.Config.FeignConfig;
import com.ytyo.Const.GeneralConst;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;

@FeignClient(value = "LiveStore", configuration = FeignConfig.class)
public interface LiveStoreFeign {
    @PostMapping(value = "/static/user/saveImage", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        //非 Map<String,?> 类型不被我们配置的FormEncoder认可，即使是 Map<String,String>也不行
    String saveImage(Map<String, ?> formParamsMap, @RequestHeader(GeneralConst.X_USER_HEADER) String userToken);

    @PostMapping(value = "/static/user/saveImages",consumes = MediaType.APPLICATION_JSON_VALUE)
    List<String> saveImages(@RequestBody Map<String, ?> map,@RequestHeader(GeneralConst.X_USER_HEADER) String userString);
}
