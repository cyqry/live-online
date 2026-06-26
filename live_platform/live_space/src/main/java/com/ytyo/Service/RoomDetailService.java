package com.ytyo.Service;

import com.ytyo.Manager.RoomDetailsManager;
import com.ytyo.Model.RoomDetails;
import com.ytyo.Option.Option;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RoomDetailService {
    @Autowired
    RoomDetailsManager roomDetailsManager;


    public boolean createRoomDetails(String path, RoomDetails.RoomInfo roomInfo) {
        RoomDetails roomDetails = new RoomDetails(roomInfo, path);
        return roomDetailsManager.putRoomDetails(roomDetails).isOk();
    }


    public void removeRoomDetailsById(Long roomId) {
        roomDetailsManager.deleteRoomDetails(roomId);
    }

    public boolean addMember(long roomId, short who, long id) {
        return roomDetailsManager.addMember(roomId, who, id) != -2;
    }

    public void deleteMember(long roomId, short who, long id) {
        if (roomDetailsManager.deleteMember(roomId, who, id) != -1) {
            log.info("没有这个房间或成员已离开");
        }
    }

    public Option<RoomDetails> getRoomDetails(long roomId) {
        return roomDetailsManager.getRoomDetails(roomId);
    }

    public Option<List<RoomDetails>> getRoomDetailsByRoomIds(Long... roomIds) {
        return roomDetailsManager.getRoomDetailsByRoomIds(roomIds);
    }
}
