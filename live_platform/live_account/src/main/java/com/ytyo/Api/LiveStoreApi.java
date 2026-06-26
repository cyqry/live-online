package com.ytyo.Api;

import com.ytyo.Const.GeneralConst;
import com.ytyo.Feign.LiveStoreFeign;
import com.ytyo.Option.Option;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
public class LiveStoreApi {
    @Autowired
    LiveStoreFeign liveStoreFeign;

    public Option<String> saveImage(String base64Param, String directoryPathParam, String imageNameParam, HttpServletRequest request) {
        if (request == null || request.getHeader(GeneralConst.X_USER_HEADER) == null) {
            return Option.None();
        }

        try {
            String result = liveStoreFeign.saveImage(Map.of("base64", base64Param, "directoryPath", directoryPathParam, "imageName", imageNameParam), request.getHeader(GeneralConst.X_USER_HEADER));
            if (StringUtils.hasText(result)) {
                return Option.Some(result);
            } else {
                return Option.None();
            }
        } catch (FeignException e) {
            return Option.None();
        }
    }
}
