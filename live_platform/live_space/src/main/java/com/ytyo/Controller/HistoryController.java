package com.ytyo.Controller;

import com.ytyo.Api.LiveAccountApi;
import com.ytyo.Model.ElasticSearch.RoomTag;
import com.ytyo.Model.RoomCard;
import com.ytyo.Model.RoomDetails;
import com.ytyo.Model.User;
import com.ytyo.Model.WatchHistory;
import com.ytyo.Option.NoneException;
import com.ytyo.Option.Option;
import com.ytyo.Service.*;
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
public class HistoryController {

    @Autowired
    WatchHistoryService watchHistoryService;

    @Autowired
    RoomTagService roomTagService;

    @Autowired
    LiveAccountApi liveAccountApi;

    @Autowired
    RoomDetailService roomDetailService;

    @Autowired
    FollowService followService;
    @Autowired
    RoomInfoService roomInfoService;

    @GetMapping("/getUserHistory")
    public ResponseEntity<?> getUserHistory(HttpServletRequest request) {

        Option<User> user = Option.None();
        Map<Long, RoomCard> roomRecords = new HashMap<>();
        try {
            user = RequestUtil.getUserByReq(request);
            Option<List<WatchHistory>> historyList = watchHistoryService.getUserWatchHistory(user.unwrap().getId());

            historyList.unwrap().forEach(history -> {
                Long roomId = history.getRoomId();
                RoomCard roomCard = new RoomCard();
                roomCard.attach(history);
                roomRecords.put(roomId, roomCard);
            });

            List<RoomTag> tags = roomTagService.getRoomTagByRoomIds(roomRecords.keySet().toArray(new Long[0])).unwrap();
            List<RoomDetails> details = roomDetailService.getRoomDetailsByRoomIds(roomRecords.keySet().toArray(new Long[0])).unwrap();
            List<RoomDetails.RoomInfo> roomInfos = roomInfoService.getRoomInfoByRoomIds(roomRecords.keySet().toArray(new Long[0])).unwrap();

            HashMap<Long, Long> anchorIdRoomIdMap = new HashMap<>();
            roomInfos.forEach(roomInfo -> {
                anchorIdRoomIdMap.put(roomInfo.getAnchorId(), roomInfo.getId());
                RoomCard roomCard = roomRecords.get(roomInfo.getId());
                if (roomCard != null) {
                    roomCard.attach(roomInfo);
                }
            });

            Map<Long, Long> countMap = followService.getFollowCounts(roomRecords.keySet().toArray(new Long[0])).unwrap();

            countMap.forEach((roomId, count) -> {
                RoomCard roomCard = roomRecords.get(roomId);
                if (roomCard != null) {
                    roomCard.setAttentions(count);
                }
            });

            List<User> userList = liveAccountApi.getUserByIds(anchorIdRoomIdMap.keySet().toArray(new Long[0])).unwrap();

            userList.forEach(u -> {
                RoomCard roomCard = roomRecords.get(anchorIdRoomIdMap.get(u.getId()));
                if (roomCard != null)
                    roomCard.attach(u);
            });

            tags.forEach(tag -> {
                RoomCard roomCard = roomRecords.get(tag.getRoomId());
                if (roomCard != null)
                    roomCard.attach(tag);
            });

            details.forEach(roomDetails -> {
                if (roomDetails != null && roomDetails.getRoomInfo() != null && roomDetails.getRoomInfo().getId() != null) {
                    RoomCard roomCard = roomRecords.get(roomDetails.getRoomInfo().getId());
                    if (roomCard != null)
                        roomCard.attach(roomDetails);
                }
            });

            return ResponseEntity.ok(roomRecords.values());
        } catch (NoneException e) {
            log.info("user: {}", user.data());
            if (user.isSome()) {
                log.info("historyList:{}", roomRecords);
            }
            return ResponseEntity.badRequest().body("查询失败!");
        }
    }


    @PostMapping("/deleteHistory")
    public ResponseEntity<?> removeHistory(Long roomId, HttpServletRequest request) {
        if (roomId == null) {
            return ResponseEntity.badRequest().body("参数错误！");
        }
        Option<User> user = RequestUtil.getUserByReq(request);

        try {
            boolean deleted = watchHistoryService.deleteHistory(user.unwrap().getId(), roomId);
            if (deleted) {
                return ResponseEntity.ok("删除成功!");
            } else {
                return ResponseEntity.badRequest().body("删除失败!");
            }
        } catch (NoneException e) {
            log.info("user :{}", user.data());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("内部错误!");
        }
    }
}
