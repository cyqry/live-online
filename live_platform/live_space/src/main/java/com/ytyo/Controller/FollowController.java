package com.ytyo.Controller;

import com.ytyo.Api.LiveAccountApi;
import com.ytyo.Model.ElasticSearch.RoomTag;
import com.ytyo.Model.RoomCard;
import com.ytyo.Model.RoomDetails;
import com.ytyo.Model.User;
import com.ytyo.Model.UserFollowRoom;
import com.ytyo.Option.NoneException;
import com.ytyo.Option.Option;
import com.ytyo.Service.FollowService;
import com.ytyo.Service.RoomDetailService;
import com.ytyo.Service.RoomInfoService;
import com.ytyo.Service.RoomTagService;
import com.ytyo.Utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class FollowController {
    @Autowired
    FollowService followService;

    @Autowired
    RoomInfoService roomInfoService;

    @Autowired
    RoomTagService roomTagService;
    @Autowired
    RoomDetailService roomDetailService;
    @Autowired
    LiveAccountApi liveAccountApi;

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(Long roomId, HttpServletRequest request) throws NoneException {
        if (roomId == null)
            return ResponseEntity.badRequest().body("错误的请求");
        User user = RequestUtil.getUserByReq(request).unwrap();


        if (user.getId() == null) {
            log.error("内部错误!");
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("服务器错误!");
        }

        if (roomInfoService.hasRoom(user.getId(), roomId)) {
            return ResponseEntity.badRequest().body("自己不能关注自己!");
        }

        if (followService.isFollow(user.getId(), roomId)) {
            return ResponseEntity.badRequest().body("已经关注!");
        }

        if (!followService.subscribe(user.getId(), roomId)) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("关注失败!");
        }
        return ResponseEntity.ok("关注成功!");
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribe(Long roomId, HttpServletRequest request) throws NoneException {
        if (roomId == null)
            return ResponseEntity.badRequest().body("错误的请求");
        User user = RequestUtil.getUserByReq(request).unwrap();

        if (!followService.unsubscribe(user.getId(), roomId)) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("取关失败!");
        }
        return ResponseEntity.ok("取关成功!");
    }

    @GetMapping("/getAttentionCount")
    public ResponseEntity<?> getAttentionCount(Long roomId) {
        if (roomId == null) {
            return ResponseEntity.badRequest().body("错误的请求");
        }
        try {
            return ResponseEntity.ok(followService.countFollowRoom(roomId).unwrap());
        } catch (NoneException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("查询失败!");
        }
    }


    /**
     * 返回400代表未关注该房间
     *
     * @param roomId
     * @param request
     * @return
     * @throws NoneException
     */
    @GetMapping("/getFollow")
    public ResponseEntity<?> getFollow(Long roomId, HttpServletRequest request) throws NoneException {
        if (roomId == null) {
            return ResponseEntity.badRequest().body("错误的请求");
        }
        User user = RequestUtil.getUserByReq(request).unwrap();
        Option<UserFollowRoom> follow = followService.getFollow(user.getId(), roomId);
        if (follow.isSome()) {
            return ResponseEntity.ok(follow.unwrap());
        } else {
            return ResponseEntity.badRequest().body("未关注");
        }
    }

    /**
     * 获取用户关注信息
     *
     * @param pageSize
     * @param request
     * @return
     */
    @GetMapping("/getFollowInfo")
    public ResponseEntity<?> getFollowInfo(int pageSize, HttpServletRequest request) {
        try {
            User user = RequestUtil.getUserByReq(request).unwrap();
            List<UserFollowRoom> follows = followService.getFollows(user.getId(), pageSize).unwrap();
            HashMap<Long, RoomCard> map = new HashMap<>();

            for (UserFollowRoom follow : follows) {
                map.put(follow.getRoomId(), new RoomCard());
            }
            HashMap<Long, Long> userIdRoomIdMap = new HashMap<>();

            List<RoomDetails.RoomInfo> roomInfos = roomInfoService.getRoomInfoByRoomIds(map.keySet().toArray(new Long[0])).unwrap();
            for (RoomDetails.RoomInfo roomInfo : roomInfos) {
                userIdRoomIdMap.put(roomInfo.getAnchorId(), roomInfo.getId());
                RoomCard roomCard = map.get(roomInfo.getId());
                if (roomCard != null) {
                    roomCard.attach(roomInfo);
                }
            }

            List<RoomDetails> roomDetails = roomDetailService.getRoomDetailsByRoomIds(map.keySet().toArray(new Long[0])).unwrap();

            for (RoomDetails detail : roomDetails) {
                RoomCard roomCard = map.get(detail.getRoomInfo().getId());
                if (roomCard != null) {
                    roomCard.attach(detail);
                }
            }
            Map<Long, Long> countMap = followService.getFollowCounts(map.keySet().toArray(new Long[0])).unwrap();

            countMap.forEach((roomId, count) -> {
                RoomCard roomCard = map.get(roomId);
                if (roomCard != null) {
                    roomCard.setAttentions(count);
                }
            });


            List<RoomTag> roomTags = roomTagService.getRoomTagByRoomIds(map.keySet().toArray(new Long[0])).unwrap();
            for (RoomTag roomTag : roomTags) {
                RoomCard roomCard = map.get(roomTag.getRoomId());
                if (roomCard != null) {
                    roomCard.attach(roomTag);
                }
            }

            List<User> anchors = liveAccountApi.getUserByIds(userIdRoomIdMap.keySet().toArray(new Long[0])).unwrap();
            for (User anchor : anchors) {
                RoomCard roomCard = map.get(userIdRoomIdMap.get(anchor.getId()));
                if (roomCard != null) {
                    roomCard.attach(anchor);
                }
            }


            return ResponseEntity.ok(map.values());
        } catch (NoneException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("查询失败!");
        }
    }
}
