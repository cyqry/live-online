package com.ytyo.Service;

import com.ytyo.Dao.RoomInfoMapper;
import com.ytyo.Manager.RoomDetailsManager;
import com.ytyo.Model.RoomDetails;
import com.ytyo.Option.Option;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
public class RoomInfoService {
    @Autowired
    RoomInfoMapper roomInfoMapper;

    @Autowired
    RoomDetailsManager roomDetailsManager;

    public boolean hasRoom(long userId, long roomId) {
        return roomInfoMapper.countRoomInfo(userId, roomId) > 0;
    }

    public Option<RoomDetails.RoomInfo> getRoomInfoByAnchorId(Long anchorId) {
        if (anchorId == null)
            return Option.None();

        RoomDetails.RoomInfo roomInfo = roomInfoMapper.selectRoomInfoByAnchorId(anchorId);
        return Option.from(roomInfo);
    }

    public boolean updateLastLiveTime(Long roomId) {
        if (roomId == null)
            return false;
        RoomDetails.RoomInfo roomInfo = new RoomDetails.RoomInfo();
        roomInfo.setId(roomId);
        roomInfo.setLastLiveTime(LocalDateTime.now());


        boolean redisUpdated = roomDetailsManager.updateRoomInfo(roomInfo);
        if (!redisUpdated) {
            log.info("房间缓存更新失败，缓存中不存在房间");
        }
        return roomInfoMapper.updateRoomInfoById(roomInfo) > 0;
    }

    public boolean roomCanLive(Long roomId) {
        return roomInfoMapper.getCanLive(roomId) == 1;
    }

    public boolean changeRoomInfoCanLive(Long roomId, short canLive) {
        if (roomId == null) {
            return false;
        }
        RoomDetails.RoomInfo roomInfo = new RoomDetails.RoomInfo();
        roomInfo.setId(roomId);
        roomInfo.setCanLive(canLive);


        boolean redisUpdated = roomDetailsManager.updateRoomInfo(roomInfo);
        if (!redisUpdated) {
            log.info("房间缓存更新失败,缓存中不存在这个房间");
        }
        return roomInfoMapper.updateRoomInfoById(roomInfo) > 0;
    }


    public Option<RoomDetails.RoomInfo> getRoomInfoById(Long roomId) {
        if (roomId == null)
            return Option.None();
        Option<RoomDetails.RoomInfo> roomInfoCache = roomDetailsManager.getRoomInfoById(roomId);
        if (roomInfoCache.isSome()) {
            return roomInfoCache;
        }
        RoomDetails.RoomInfo roomInfo = roomInfoMapper.selectRoomInfoById(roomId);
        return Option.from(roomInfo);
    }

    public Option<List<RoomDetails.RoomInfo>> getRoomInfoByRoomIds(Long... roomIds) {
        if (roomIds == null)
            return Option.None();
        if (roomIds.length == 0)
            return Option.Some(new ArrayList<>());
        //批量查询不可走缓存
        List<RoomDetails.RoomInfo> roomInfos = roomInfoMapper.selectRoomInfoByIds(Stream.of(roomIds).toList());
        return Option.from(roomInfos);
    }

    public Option<List<RoomDetails.RoomInfo>> getRoomInfoByAnchorIds(Long... anchorIds) {
        if (anchorIds == null)
            return Option.None();
        if (anchorIds.length == 0)
            return Option.Some(new ArrayList<>());

        //批量查询不可走缓存
        List<RoomDetails.RoomInfo> roomInfos = roomInfoMapper.selectRoomInfoByAnchorIds(Stream.of(anchorIds).toList());
        return Option.from(roomInfos);
    }


    public boolean isRoomAnchor(Long roomId, Long userId) {
        return roomInfoMapper.countRoomIdAnchorId(roomId, userId) > 0;
    }
}
