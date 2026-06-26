package com.ytyo.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ytyo.Api.LiveSpaceApi;
import com.ytyo.Model.RoomWebRtcStore;
import com.ytyo.Model.User;
import com.ytyo.Option.NoneException;
import com.ytyo.Option.Option;
import com.ytyo.Util.EnterUtil;
import com.ytyo.Worker.RoomWebRtcStoreManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@Slf4j
public class RoomHandler implements ContextOnLiveHandler {

    @Autowired
    RoomWebRtcStoreManager roomWebRtcStoreManager;
    @Autowired
    LiveSpaceApi liveSpaceApi;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void preOpenHandler(WebSocketSession session) {
        try {
            short who = EnterUtil.takeWhoOf(session);
            long roomId = EnterUtil.takeRoomIdOf(session);
            long id = EnterUtil.takeIdOf(session);
            Option<User> user = EnterUtil.takeUserOf(session);

            if (who == 1) {
                if (!liveSpaceApi.updateLastLiveTime(roomId, user.unwrap())) {
                    log.error("更新最后直播时间失败");
                }
            } else {
                if (!liveSpaceApi.EnterRoom(roomId, who, id, user)) {
                    throw new RuntimeException("进入房间失败!");
                }
            }
        } catch (NoneException e) {
            log.error("没有user，奇怪");
        }
    }

    @Override
    public void preCloseHandler(WebSocketSession session) {
        try {
            short who = EnterUtil.takeWhoOf(session);
            long roomId = EnterUtil.takeRoomIdOf(session);
            long id = EnterUtil.takeIdOf(session);
            Option<User> user = EnterUtil.takeUserOf(session);

            if (who == 1) {//主播下播后，必须删除Webrtc房间，Redis房间可以不删，可开启定时任务删除；主播rtc又建连之后，自动重新同步(那么就是重新创建)Webrtc房间,并终止定时任务

                //由于如果这里是被自己重新开播挤了之后断开,只能通过roomId删除redis房间，会导致刚创建的redis房间被删除，那么也让 新的webrtc和redis room 一起下线，所以直接根据房间id找，不根据原主播session
                Option<RoomWebRtcStore> roomWebRtcStore = roomWebRtcStoreManager.getRoomWebRtcStore(roomId);
                if (roomWebRtcStore.isSome()) {
                    //因为没有实现乐观锁，所以不提供根据roomId删除的方法
                    roomWebRtcStoreManager.removeRoom(roomWebRtcStore.unwrap());
                }
                if (!liveSpaceApi.offLineRoom(roomId, user.unwrap())) {
                    log.error("房间下线失败");
                }
                liveSpaceApi.updateLastLiveTime(roomId, user.unwrap());
                log.info("主播下播");
            } else {
                liveSpaceApi.LeaveRoom(roomId, who, id, user);
            }
        } catch (NoneException e) {
            log.error("在Session中应存储User而未存储");
        }
    }
}
