package com.ytyo.Feign;

import com.ytyo.Config.FeignConfig;
import com.ytyo.Const.GeneralConst;
import com.ytyo.Model.Anchor;
import com.ytyo.Model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "LiveAccount", configuration = FeignConfig.class)
public interface LiveAccountFeign {
    @GetMapping("/getAnchor")
    Anchor getAnchor(@RequestHeader(GeneralConst.X_USER_HEADER) String userToken);

    @GetMapping("/isAnchor")
    Boolean isAnchor(@RequestHeader(GeneralConst.X_USER_HEADER) String userToken);

    @GetMapping("/getAnchorById")
    Anchor getAnchorById(@RequestParam long anchorId);

    @PostMapping("/getUserByIds")
    List<User> getUserByIds(@RequestBody List<Long> userIds);


}
