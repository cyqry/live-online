package com.ytyo.Controller;

import com.ytyo.annotation.authority.Remote;
import com.ytyo.Model.RoomWebRtcStore;
import com.ytyo.Option.NoneException;
import com.ytyo.Option.Option;
import com.ytyo.Worker.RoomWebRtcStoreManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CtrlController {
    @Autowired
    RoomWebRtcStoreManager roomWebRtcStoreManager;

    @PostMapping("/offlineRoom")
    @Remote
    public ResponseEntity<?> offlineRoom(Long roomId) throws NoneException {
        if (roomId == null) {
            return ResponseEntity.badRequest().body("错误的请求！");
        }
        Option<RoomWebRtcStore> roomWebRtcStore = roomWebRtcStoreManager.getRoomWebRtcStore(roomId);
        if (roomWebRtcStore.isSome()) {
            roomWebRtcStoreManager.removeRoom(roomWebRtcStore.unwrap());
        }
        return ResponseEntity.ok("下线房间成功!");
    }
}
