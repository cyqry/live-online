package com.ytyo.Controller;


import com.ytyo.annotation.authority.Remote;
import com.ytyo.annotation.authority.RemoteNoLoginRequired;
import com.ytyo.Api.LiveAccountApi;
import com.ytyo.Api.LiveStoreApi;
import com.ytyo.Model.Anchor;
import com.ytyo.Model.RoomDetails;
import com.ytyo.Model.RoomPublic;
import com.ytyo.Model.User;
import com.ytyo.Option.NoneException;
import com.ytyo.Option.Option;
import com.ytyo.Service.RoomDetailService;
import com.ytyo.Service.RoomInfoService;
import com.ytyo.Service.RoomTagService;
import com.ytyo.Service.WatchHistoryService;
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

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
public class RoomDetailController {

    @Autowired
    RoomDetailService roomDetailService;

    @Autowired
    RoomTagService roomTagService;

    @Autowired
    RoomInfoService roomInfoService;

    @Autowired
    LiveAccountApi liveAccountApi;

    @Autowired
    WatchHistoryService watchHistoryService;
    @Autowired
    LiveStoreApi liveStoreApi;

    @PostMapping("/createRoom")
    public ResponseEntity<?> createRoom(String coverBase64, String roomTitle, HttpServletRequest request) {
        if (coverBase64 == null || roomTitle == null)
            return ResponseEntity.badRequest().body("请求参数错误!");
        try {
            Anchor anchor = liveAccountApi.getAnchor(request).unwrap();
            //by id找的话返回对象不包含查询字段
            RoomDetails.RoomInfo roomInfo = roomInfoService.getRoomInfoByAnchorId(anchor.getId()).unwrap();
            if (roomInfo.getCanLive() == 0)
                return ResponseEntity.badRequest().body("封禁中");
            boolean updated = roomTagService.updateTitle(roomTitle, roomInfo.getId());
            if (!updated)
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("更新标题失败!");

            String path = liveStoreApi.saveImage(coverBase64, "/cover", UUID.randomUUID().toString(), request).unwrap();

            boolean created = roomDetailService.createRoomDetails(path, roomInfo);
            if (!created)
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("创建房间失败!");
            return ResponseEntity.ok("创建房间成功!");
        } catch (NoneException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("创建房间失败!");
        }
    }

    @PostMapping("/offlineRoom")
    @Remote
    public ResponseEntity<?> offlineRoom(Long roomId, HttpServletRequest request) {
        Option<Anchor> anchor = liveAccountApi.getAnchor(request);
        if (anchor.isNone()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("没有权限删除");
        }
        roomDetailService.removeRoomDetailsById(roomId);
        return ResponseEntity.ok("删除房间成功!");

    }


    @PostMapping("/enterRoom")
    @RemoteNoLoginRequired
    @Remote
    public ResponseEntity<?> enterRoom(Long roomId, Short who, Long id, HttpServletRequest request) throws NoneException {
        if (who == null || roomId == null || (who != 0 && who != -1) || id == null)
            return ResponseEntity.badRequest().body("进入房间 参数错误！");
        Option<User> user = RequestUtil.getUserByReq(request);

        if (user.isSome() && who == 0 && user.unwrap().getId().equals(id)) {
            boolean history = watchHistoryService.updateOrPutHistory(roomId, id);
            if (!history) {
                log.error("历史记录更新失败!");
            }
        } else if (user.isNone() && who == -1) {//游客访问，加入房间就行
        } else {
            log.error("收到参数发生未预测错误 id:{} user:{}", id, user.data());
            return ResponseEntity.badRequest().body("错误!");
        }

        if (!roomDetailService.addMember(roomId, who, id)) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("加入房间失败!");
        } else {
            return ResponseEntity.ok("加入房间成功!");
        }
    }


    @PostMapping("/leaveRoom")
    @Remote
    @RemoteNoLoginRequired
    public ResponseEntity<?> leaveRoom(Long roomId, Short who, Long id, HttpServletRequest request) throws NoneException {

        if (who == null || roomId == null || (who != 0 && who != -1) || id == null)
            return ResponseEntity.badRequest().body("离开房间参数错误！");
        Option<User> user = RequestUtil.getUserByReq(request);

        if (user.isSome() && who == 0 && user.unwrap().getId().equals(id)) {
            boolean history = watchHistoryService.updateOrPutHistory(roomId, id);
            if (!history) {
                log.error("历史记录更新失败!");
            }
        } else if (user.isNone() && who == -1) {//游客访问，离开房间就行
        } else {
            log.error("收到参数发生未预测错误 id:{} user:{}", id, user.data());
            return ResponseEntity.badRequest().body("错误!");
        }

        roomDetailService.deleteMember(roomId, who, id);
        return ResponseEntity.ok("离开房间成功!");
    }

    @GetMapping("/getRoom")
    @Remote
    @RemoteNoLoginRequired
    public ResponseEntity<?> getRoomDetails(Long roomId) {
        if (roomId == null)
            return ResponseEntity.badRequest().body("参数错误！");
        try {
            return ResponseEntity.ok(roomDetailService.getRoomDetails(roomId).unwrap());
        } catch (NoneException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("未找到房间");
        }
    }

    @PostMapping("/getRooms")
    @Remote
    @RemoteNoLoginRequired
    public ResponseEntity<?> getRooms(@RequestBody List<Long> roomIds) {
        if (roomIds == null)
            return ResponseEntity.badRequest().body("参数错误！");
        try {
            return ResponseEntity.ok(roomDetailService.getRoomDetailsByRoomIds(roomIds.toArray(new Long[0])).unwrap());
        } catch (NoneException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("未找到房间");
        }
    }

    /**
     * RoomDetails的公开部分
     *
     * @param roomId
     * @return
     * @throws NoneException
     */
    @GetMapping("/public/getRoomPublic")
    @RemoteNoLoginRequired
    public ResponseEntity<?> getRoomPublic(Long roomId) throws NoneException {
        if (roomId == null)
            return ResponseEntity.badRequest().body("错误的请求!");
        Option<RoomDetails> roomDetailsOption = roomDetailService.getRoomDetails(roomId);
        if (roomDetailsOption.isSome()) {
            RoomDetails roomDetails = roomDetailsOption.unwrap();
            return ResponseEntity.ok(new RoomPublic(roomDetails.getRoomInfo(), roomDetails.getUserList().size() + roomDetails.getVisitorList().size(), roomDetails.getCoverSrc()));
        } else {
            return ResponseEntity.badRequest().body("房间不在线");
        }
    }

}
