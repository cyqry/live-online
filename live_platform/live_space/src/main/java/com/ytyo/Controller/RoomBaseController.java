package com.ytyo.Controller;

import com.ytyo.annotation.authority.Remote;
import com.ytyo.annotation.authority.RemoteNoLoginRequired;
import com.ytyo.Api.LiveAccountApi;
import com.ytyo.Model.*;
import com.ytyo.Model.ElasticSearch.RoomTag;
import com.ytyo.Option.NoneException;
import com.ytyo.Option.Option;
import com.ytyo.Service.RoomBaseService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
public class RoomBaseController {

    @Autowired
    RoomBaseService roomBaseService;
    @Autowired
    RoomTagService roomTagService;
    @Autowired
    RoomInfoService roomInfoService;
    @Autowired
    LiveAccountApi liveAccountApi;
    @Autowired
    RoomDetailService roomDetailService;

    @PostMapping("/createRoomBase")
    @Remote
    public ResponseEntity<?> createRoomBase(String roomItemCategoryId, HttpServletRequest request) {
        try {
            Option<User> user = RequestUtil.getUserByReq(request);
            boolean created = roomBaseService.createRoomBase(user.unwrap().getId(), roomItemCategoryId);
            if (!created)
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("创建RoomBase失败");
            return ResponseEntity.ok("创建成功！");
        } catch (Exception e) {
            log.error("创建RoomBase失败", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("创建RoomBase失败");
        }
    }

    @PostMapping("/updateLastLiveTime")
    @Remote
    public ResponseEntity<?> updateLastLiveTime(Long roomId) {
        if (roomId == null)
            return ResponseEntity.badRequest().body("错误的请求");
        boolean updated = roomInfoService.updateLastLiveTime(roomId);
        if (updated)
            return ResponseEntity.ok("更新成功!");
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("更新失败!");
    }


//    getRoomInfosByAnchorIds


    @PostMapping("/getRoomInfosByAnchorIds")
    @Remote
    @RemoteNoLoginRequired
    public ResponseEntity<?> getRoomInfosByAnchorIds(@RequestBody List<Long> anchorIds) {
        if (anchorIds == null)
            return ResponseEntity.badRequest().body("错误的请求！");
        try {
            List<RoomDetails.RoomInfo> infos = roomInfoService.getRoomInfoByAnchorIds(anchorIds.toArray(new Long[0])).unwrap();
            return ResponseEntity.ok(infos);
        } catch (NoneException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("查询失败!");
        }
    }

    @PostMapping("/getRoomInfos")
    @Remote
    @RemoteNoLoginRequired
    public ResponseEntity<?> getRoomInfos(@RequestBody List<Long> roomIds) {
        if (roomIds == null)
            return ResponseEntity.badRequest().body("错误的请求！");
        try {
            List<RoomDetails.RoomInfo> infos = roomInfoService.getRoomInfoByRoomIds(roomIds.toArray(new Long[0])).unwrap();
            return ResponseEntity.ok(infos);
        } catch (NoneException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("查询失败!");
        }
    }

    @GetMapping("/getRoomInfo")
    @RemoteNoLoginRequired
    public ResponseEntity<?> getRoomInfo(Long roomId, HttpServletRequest request) {
        Option<Anchor> anchor = Option.None();
        if (roomId == null) {
            anchor = liveAccountApi.getAnchor(request);
            if (anchor.isNone())
                return ResponseEntity.badRequest().body("错误的请求");
        }
        try {
            RoomDetails.RoomInfo info;
            if (roomId != null)
                info = roomInfoService.getRoomInfoById(roomId).unwrap();
            else {
                info = roomInfoService.getRoomInfoByAnchorId(anchor.unwrap().getId()).unwrap();
            }
            return ResponseEntity.ok(info);
        } catch (NoneException e) {
            return ResponseEntity.badRequest().body("查询房间信息失败");
        }
    }

    @GetMapping("/public/getRoomBase")
    @RemoteNoLoginRequired
    public ResponseEntity<?> getRoomBase(Long roomId) {
        if (roomId == null)
            return ResponseEntity.badRequest().body("错误的请求");
        try {
            RoomTag roomTag = roomTagService.getRoomTagByRoomId(roomId).unwrap();
            RoomDetails.RoomInfo roomInfo = roomInfoService.getRoomInfoById(roomId).unwrap();
            return ResponseEntity.ok(new RoomBase(roomInfo, roomTag));
        } catch (NoneException e) {
            return ResponseEntity.badRequest().body("未找到房间信息");
        }
    }


    @GetMapping("/public/searchRelativeRoomBase")
    public ResponseEntity<?> searchRoomBase(String condition, Integer pageSize) {
        if (condition == null) {
            return ResponseEntity.badRequest().body("错误的参数!");
        }
        if (pageSize == null || pageSize == 0)
            pageSize = 10;

        try {
            HashMap<Long, RoomSearch> map = new HashMap<>();
            try {
                Long roomId = Long.valueOf(condition);
                RoomTag roomTag = roomTagService.getRoomTagByRoomId(roomId).unwrap();
                map.put(roomId, new RoomSearch(roomTag));
            } catch (NoneException | NumberFormatException e) {
                Option<List<RoomTag>> tags = roomTagService.selectRoomTagByCondition(condition, pageSize);
                if (tags.isSome()) {
                    List<RoomTag> roomTags = tags.unwrap();
                    for (RoomTag roomTag : roomTags) {
                        map.put(roomTag.getRoomId(), new RoomSearch(roomTag));
                    }
                } else {
                    return ResponseEntity.ok(new ArrayList<>());
                }
            }

            List<RoomDetails> details = roomDetailService.getRoomDetailsByRoomIds(map.keySet().toArray(new Long[0])).unwrap();
            HashMap<Long, Long> anchorIdRoomIdMap = new HashMap<>();
            for (RoomDetails detail : details) {
                RoomSearch roomSearch = map.get(detail.getRoomInfo().getId());
                if (roomSearch != null) {
                    roomSearch.attach(detail);
                }
                anchorIdRoomIdMap.put(detail.getRoomInfo().getAnchorId(), detail.getRoomInfo().getId());
            }

            //剔除不在线的房间
            map.entrySet().removeIf((s) -> details.stream().noneMatch(d -> Objects.equals(d.getRoomInfo().getId(), s.getKey())));


            List<User> users = liveAccountApi.getUserByIds(anchorIdRoomIdMap.keySet().toArray(new Long[0])).unwrap();
            for (User user : users) {
                RoomSearch roomSearch = map.get(anchorIdRoomIdMap.get(user.getId()));
                if (roomSearch != null) {
                    roomSearch.attach(user);
                }
            }
            return ResponseEntity.ok(map.values());
        } catch (NoneException e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }


    @GetMapping("/getRoomTag")
    @RemoteNoLoginRequired
    public ResponseEntity<?> getRoomTag(Long roomId) {
        if (roomId == null)
            return ResponseEntity.badRequest().body("错误的请求");
        try {
            RoomTag roomTag = roomTagService.getRoomTagByRoomId(roomId).unwrap();
            return ResponseEntity.ok(roomTag);
        } catch (NoneException e) {
            return ResponseEntity.badRequest().body("查询tag信息失败");
        }
    }
}
