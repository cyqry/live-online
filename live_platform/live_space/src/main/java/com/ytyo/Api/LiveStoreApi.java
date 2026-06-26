package com.ytyo.Api;

import com.ytyo.Const.GeneralConst;
import com.ytyo.Feign.LiveStoreFeign;
import com.ytyo.Option.Option;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class LiveStoreApi {

    @Autowired
    LiveStoreFeign liveStoreFeign;

    public Option<String> saveImage(String base64, String directoryPath, String imageName, HttpServletRequest request) {
        String userString = request.getHeader(GeneralConst.X_USER_HEADER);
        if (!StringUtils.hasText(userString))
            return Option.None();
        try {
            String path = liveStoreFeign.saveImage(Map.of("base64", base64, "directoryPath", directoryPath, "imageName", imageName), userString);
            return Option.from(path);
        } catch (FeignException e) {
            return Option.None();
        }
    }

    public Option<List<String>> saveImages(String[] base64s, String directoryPath, HttpServletRequest request) {
        String userString = request.getHeader(GeneralConst.X_USER_HEADER);
        if (!StringUtils.hasText(userString) || base64s == null || directoryPath == null)
            return Option.None();
        if (base64s.length == 0)
            return Option.Some(Collections.emptyList());

        try {
            return Option.from(liveStoreFeign.saveImages(Map.of("base64s", base64s, "directoryPath", directoryPath), userString));
        } catch (FeignException e) {
            return Option.None();
        }
//        String url = "http://localhost:8083/static/user/saveImages";
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set(CommonConst.X_USER_HEADER, userString);
//        headers.set(RemoteConst.REMOTE_INVOKE_HEADER, RemoteConst.REMOTE_INVOKE_TOKEN);
//
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("base64s", base64s);
//        hashMap.put("directoryPath", directoryPath);
//
//        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(hashMap, headers);
//        ResponseEntity<List<String>> result = restTemplate.exchange(url, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {
//        });
//
//        if (result.getStatusCode().is2xxSuccessful())
//            return Option.from(result.getBody());
//        return Option.None();
    }
}
