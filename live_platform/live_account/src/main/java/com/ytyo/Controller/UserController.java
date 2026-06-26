package com.ytyo.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ytyo.annotation.authority.Remote;
import com.ytyo.annotation.authority.RemoteNoLoginRequired;
import com.ytyo.Api.LiveSpaceApi;
import com.ytyo.Model.*;
import com.ytyo.Option.NoneException;
import com.ytyo.Option.Option;
import com.ytyo.Service.*;
import com.ytyo.Utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
@Validated
public class UserController {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserService userService;

    @Autowired
    AnchorService anchorService;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    LiveSpaceApi liveSpaceApi;

    @Autowired
    GiftService giftService;

    @Autowired
    StoreService storeService;

    @GetMapping(value = "/getUser")
    public ResponseEntity<?> getUser(HttpServletRequest request) {
        try {
            User user = RequestUtil.getUserByReq(request).unwrap();
            return ResponseEntity.ok(user);
        } catch (NoneException e) {
            log.error("网关错误");
            return ResponseEntity.badRequest().body("未登录");
        }
    }


    @GetMapping(value = "/getWholeUser")
    public ResponseEntity<?> getWholeUser(HttpServletRequest request) {
        Option<User> user = RequestUtil.getUserByReq(request);

        try {
            Option<Anchor> anchor = anchorService.selectAnchor(user.unwrap().getId());
            return ResponseEntity.ok(new WholeUser(user.unwrap(), anchor.unwrap()));
        } catch (NoneException e) {
            return ResponseEntity.badRequest().body("你还不是主播!");
        }
    }


    @PostMapping("/identity")
    public ResponseEntity<?> identity(String idNumber, String realName, HttpServletRequest request) {
        if (!StringUtils.hasText(idNumber) || !StringUtils.hasText(realName)) {
            return ResponseEntity.badRequest().body("请求参数错误");
        }
        try {
            User loginUser = RequestUtil.getUserByReq(request).unwrap();
            String cookieValue = RequestUtil.getCookieValueByReq(request).unwrap();
            authenticationService.verifyIdentity(idNumber, realName);
            User updateUser = new User();
            updateUser.setId(loginUser.getId());
            updateUser.setIdNumber(idNumber);
            updateUser.setRealName(realName);
            if (!userService.updateUserByIdCookie(updateUser, cookieValue)) {
                return ResponseEntity.badRequest().body("身份校验失败");
            }
            return ResponseEntity.ok("身份校验成功");
        } catch (NoneException e) {
            log.error("网关错误");
            return ResponseEntity.badRequest().body("校验失败");
        }
    }


    @PostMapping("/getUserByIds")
    @RemoteNoLoginRequired
    @Remote
    public ResponseEntity<?> getUserByIds(@RequestBody List<Long> userIds) {
        if (userIds == null) {
            return ResponseEntity.badRequest().body("错误的请求");
        }
        try {
            return ResponseEntity.ok(userService.getUserByIds(userIds).unwrap());
        } catch (NoneException e) {
            return ResponseEntity.badRequest().body("发生错误");
        }
    }

    @Test
    public void test() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.readValue("{\"currencies\":[{\"currencyId\":1,\"count\":200},{\"currencyId\":2,\"count\":2000}]}", Property.class));
    }

    @PostMapping("/sendGift")
    public ResponseEntity<?> sendGift(@RequestBody SendGiftEntity sendGiftEntity, HttpServletRequest request) {
        if (sendGiftEntity == null || sendGiftEntity.roomId() == null || sendGiftEntity.gifts() == null || sendGiftEntity.gifts().isEmpty()) {
            return ResponseEntity.badRequest().body("请求参数错误");
        }
        try {
            User user = RequestUtil.getUserByReq(request).unwrap();
            String cookieValue = RequestUtil.getCookieValueByReq(request).unwrap();
            AnchorProperty anchorProperty = new AnchorProperty(sendGiftEntity.gifts());


            RoomDetails.RoomInfo roomInfo = liveSpaceApi.getRoomInfo(sendGiftEntity.roomId()).unwrap();
            Long anchorId = roomInfo.getAnchorId();


            if (!giftService.sendGiftToAnchor(user.getId(), anchorId, anchorProperty, cookieValue)) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("送礼失败");
            }
            return ResponseEntity.ok("赠送成功");
        } catch (NoneException | RuntimeException e) {
            log.error("送礼失败:", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("赠送失败!");
        }


    }

    @GetMapping("/getUserByPersonalityId")
    public ResponseEntity<?> getUserByPersonalityId(@RequestParam("personalityId") String personalityId) {
        try {
            return ResponseEntity.ok(new UserPublic(userService.getUserByPersonalityId(personalityId).unwrap()));
        } catch (NoneException e) {
            return ResponseEntity.badRequest().body("未找到用户");
        }
    }


    @PostMapping("/updateUser")
    public ResponseEntity<?> updateUser(@Validated @RequestBody UserExtension userExtension, HttpServletRequest
            request) {
        try {

            if (userExtension == null) {
                return ResponseEntity.badRequest().body("参数错误");
            }
            //拒绝这几个字段
            userExtension.setRealName(null);
            userExtension.setIdNumber(null);
            userExtension.setProperty(null);
            userExtension.setBalance(null);

            String cookieValue = RequestUtil.getCookieValueByReq(request).unwrap();
            User loginUser = RequestUtil.getUserByReq(request).unwrap();

            userExtension.setId(loginUser.getId());
            //如果有新密码，但是输入旧密码错误，那么返回错误
            if (userExtension.getPassword() != null && !authenticationService.verifyPassword(userExtension.getOldPassword(), loginUser.getPassword())) {
                return ResponseEntity.badRequest().body("旧密码错误");
            }

            if (userExtension.getEmail() != null && userService.existEmail(userExtension.getEmail())) {
                return ResponseEntity.badRequest().body("邮箱已经被绑定!");
            }
            if (userExtension.getPhone() != null && userService.existPhone(userExtension.getPhone())) {
                return ResponseEntity.badRequest().body("手机号已经绑定!");
            }


            if (userExtension.getPersonalityId() != null) {
                Option<User> oldUser = userService.getUserByPersonalityId(userExtension.getPersonalityId());
                if (oldUser.isSome() && !Objects.equals(oldUser.unwrap().getId(), loginUser.getId())) {
                    return ResponseEntity.badRequest().body("个性id已存在!");
                } else {
                    if (oldUser.isSome()) {//说明个性id不变，不用改
                        userExtension.setPersonalityId(null);
                    }
                }
            }


            if (userExtension.getAvatarBase() != null) {
                String path = storeService.saveAvatar(userExtension.getAvatarBase(), cookieValue, request).unwrap();
                userExtension.setAvatar(path);
            }

            boolean updated = userService.updateUserByIdCookie(new User(userExtension), cookieValue);
            if (updated) {
                return ResponseEntity.ok("修改成功!");
            } else {
                log.info("用户不存在或参数错误:{}", userExtension);
                return ResponseEntity.badRequest().body("修改失败!");
            }

        } catch (NoneException e) {
            return ResponseEntity.badRequest().body("修改失败!");
        }
    }

    @PostMapping("/recharge")
    public ResponseEntity<?> recharge(@RequestBody Property property, HttpServletRequest request) {
        if (property == null || property.getCurrencies() == null || property.getCurrencies().isEmpty()) {
            return ResponseEntity.badRequest().body("错误的参数");
        }
        try {
            User user = RequestUtil.getUserByReq(request).unwrap();
            String cookieValue = RequestUtil.getCookieValueByReq(request).unwrap();
            if (!userService.recharge(user.getId(), property, cookieValue)) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("充值失败");
            } else {
                return ResponseEntity.ok("充值成功!");
            }
        } catch (NoneException e) {
            return ResponseEntity.badRequest().body("未登录");
        }
    }


}
