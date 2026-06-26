package com.ytyo.Controller;

import com.ytyo.annotation.authority.Remote;
import com.ytyo.annotation.authority.RemoteNoLoginRequired;
import com.ytyo.Api.LiveSpaceApi;
import com.ytyo.Model.*;
import com.ytyo.Option.NoneException;
import com.ytyo.Option.Option;
import com.ytyo.Service.AnchorService;
import com.ytyo.Service.AuthenticationService;
import com.ytyo.Service.UserService;
import com.ytyo.Utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


@RestController
@Slf4j
public class AnchorController {

    @Autowired
    AnchorService anchorService;

    @Autowired
    UserService userService;
    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    LiveSpaceApi liveSpaceApi;


    @GetMapping("/public/getAnchorPublic")
    @RemoteNoLoginRequired
    public ResponseEntity<?> getAnchorPublic(Long roomId) {
        if (roomId == null) {
            return ResponseEntity.badRequest().body("错误的请求");
        }
        try {
            RoomDetails.RoomInfo roomInfo = liveSpaceApi.getRoomInfo(roomId).unwrap();
            User anchor = userService.getUserById(roomInfo.getAnchorId()).unwrap();
            //                                                                                             判断是否在线
            return ResponseEntity.ok(new AnchorPublic(anchor.getNickname(), anchor.getAvatar(), roomId, liveSpaceApi.getRoom(roomId).isSome()));
        } catch (NoneException e) {
            return ResponseEntity.badRequest().body("未找到");
        }
    }

    @GetMapping("/isAnchor")
    public ResponseEntity<?> isAnchor(HttpServletRequest request) {
        Option<User> user = RequestUtil.getUserByReq(request);
        try {
            return ResponseEntity.ok(anchorService.existAnchor(user.unwrap().getId()));
        } catch (NoneException e) {
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/getRoomAdmins")
    public ResponseEntity<?> getRoomAdmins(HttpServletRequest request) {
        try {
            User user = RequestUtil.getUserByReq(request).unwrap();
            boolean isAnchor = anchorService.existAnchor(user.getId());
            if (!isAnchor) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权访问");
            }
            List<Long> userIdList = liveSpaceApi.getRoomAdminIds(user.getId()).unwrap();
            List<User> userList = userService.getUserByIds(userIdList).unwrap();
            ArrayList<UserPublic> result = new ArrayList<>();
            for (User u : userList) {
                result.add(new UserPublic(u));
            }
            return ResponseEntity.ok(result);
        } catch (NoneException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("查询失败!");
        }
    }

    @GetMapping("/getAnchorById")
    @Remote
    public ResponseEntity<?> getAnchorById(Long anchorId) {
        if (anchorId == null) {
            return ResponseEntity.badRequest().body("错误的请求");
        }

        try {
            Option<Anchor> anchor = anchorService.selectAnchor(anchorId);
            return ResponseEntity.ok(anchor.unwrap());
        } catch (NoneException e) {
            return ResponseEntity.badRequest().body("未找到主播");
        }
    }

    @PostMapping("/addRoomAdmin")
    public ResponseEntity<?> addRoomAdmin(String personalityId, HttpServletRequest request) {
        if (personalityId == null)
            return ResponseEntity.badRequest().body("错误的请求");

        try {
            User loginUser = RequestUtil.getUserByReq(request).unwrap();
            if (Objects.equals(loginUser.getPersonalityId(), personalityId)) {
                return ResponseEntity.badRequest().body("自己不能添加自己为房管");
            }
            boolean existAnchor = anchorService.existAnchor(loginUser.getId());
            if (!existAnchor)
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权访问");

            User targetUser = userService.getUserByPersonalityId(personalityId).unwrap();

            boolean add = liveSpaceApi.addRoomAdmin(loginUser.getId(), targetUser.getId());

            if (!add) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("添加未成功");
            }
            return ResponseEntity.ok("添加成功");
        } catch (NoneException e) {
            return ResponseEntity.badRequest().body("添加失败");
        }

    }

    @PostMapping("/deleteRoomAdmin")
    public ResponseEntity<?> deleteRoomAdmin(String personalityId, HttpServletRequest request) {
        if (personalityId == null)
            return ResponseEntity.badRequest().body("错误的请求");

        try {
            User loginUser = RequestUtil.getUserByReq(request).unwrap();

            boolean existAnchor = anchorService.existAnchor(loginUser.getId());
            if (!existAnchor)
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权访问");

            User targetUser = userService.getUserByPersonalityId(personalityId).unwrap();

            boolean deleted = liveSpaceApi.deleteRoomAdmin(loginUser.getId(), targetUser.getId());

            if (!deleted) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("操作失败");
            }
            return ResponseEntity.ok("删除成功");
        } catch (NoneException e) {
            return ResponseEntity.badRequest().body("删除失败");
        }

    }

    @GetMapping("/getAnchor")
    public ResponseEntity<?> getAnchor(HttpServletRequest request) {
        Option<User> user = RequestUtil.getUserByReq(request);
        try {
            Option<Anchor> anchor = anchorService.selectAnchor(user.unwrap().getId());
            if (anchor.isSome()) {
                return ResponseEntity.ok(anchor.unwrap());
            } else {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("还不是主播!");
            }

        } catch (NoneException e) {
            return ResponseEntity.badRequest().body("未登录");
        }
    }

    @GetMapping("/public/searchRelativeAnchor")
    public ResponseEntity<?> searchRelativeAnchor(String condition) {
        if (!StringUtils.hasText(condition)) {
            return ResponseEntity.badRequest().body("错误的请求");
        }
        try {
            List<User> users = anchorService.blurrySelectAnchorByNickname(condition).unwrap();
            Map<Long, User> idUserMap = new HashMap<>();
            for (User user : users) {
                idUserMap.put(user.getId(), user);
            }

            List<RoomDetails.RoomInfo> roomInfos = liveSpaceApi.getRoomInfosByAnchorIds(idUserMap.keySet().toArray(new Long[0])).unwrap();

            ArrayList<Long> roomIds = new ArrayList<>();

            List<AnchorPublic> result = new ArrayList<>();
            for (RoomDetails.RoomInfo roomInfo : roomInfos) {
                roomIds.add(roomInfo.getId());
                User user = idUserMap.get(roomInfo.getAnchorId());
                if (user != null) {
                    result.add(new AnchorPublic(user.getNickname(), user.getAvatar(), roomInfo.getId(), false));
                }
            }

            List<RoomDetails> details = liveSpaceApi.getRoomByRoomIds(roomIds.toArray(new Long[0])).unwrap();

            for (AnchorPublic anchorPublic : result) {
                anchorPublic.setOnline(details.stream().anyMatch((d -> Objects.equals(anchorPublic.getRoomId(), d.getRoomInfo().getId()))));
            }

            return ResponseEntity.ok(result);
        } catch (NoneException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("查询失败!");
        }
    }

    @PostMapping("/registerAnchor")
    public ResponseEntity<?> register(@Validated AnchorRegistrationInfo registrationInfo, HttpServletRequest request) {
        if (registrationInfo == null) {
            return ResponseEntity.badRequest().body("参数错误");
        }
        Option<User> user = RequestUtil.getUserByReq(request);
        Option<String> cookieValue = RequestUtil.getCookieValueByReq(request);
        if (user.isNone() || cookieValue.isNone()) {
            return ResponseEntity.badRequest().body("未登录");
        }
        try {
            if (!authenticationService.verifyPassword(user.unwrap().getPassword(), registrationInfo.getPassword()))
                return ResponseEntity.badRequest().body("密码错误");

            if (!authenticationService.verifyIdentity(registrationInfo.getIdNumber(), registrationInfo.getRealName()))
                return ResponseEntity.badRequest().body("身份校验失败");


            if (!anchorService.anchorRegister(user.unwrap(), registrationInfo, cookieValue.unwrap())) {
                return ResponseEntity.badRequest().body("注册失败!");
            }
            //...
            return ResponseEntity.ok("注册成功");
        } catch (Exception e) {
            log.error("注册失败:", e);
            return ResponseEntity.badRequest().body("注册失败!");
        }

    }
}
