package com.ytyo.Service;

import com.ytyo.Dao.RoomAdminMapper;
import com.ytyo.Model.RoomAdmin;
import com.ytyo.Option.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomAdminService {
    @Autowired
    RoomAdminMapper roomAdminMapper;

    public boolean isRoomAdmin(Long userId, Long roomId) {
        if (userId == null || roomId == null) {
            return false;
        }
        return roomAdminMapper.countRoomAdmin(userId, roomId) > 0;
    }

    public Option<List<Long>> selectRoomAdminsByRoomId(long roomId) {
        return Option.from(roomAdminMapper.selectUserIdsByRoomId(roomId));
    }

    public Option<List<RoomAdmin>> selectRoomAdminsByUserId(long userId) {
        return Option.from(roomAdminMapper.selectRoomIdsByUserId(userId));
    }

    public boolean addRoomAdmin(long userId, long roomId) {
        RoomAdmin roomAdmin = new RoomAdmin();
        roomAdmin.setRoomId(roomId);
        roomAdmin.setUserId(userId);
        return roomAdminMapper.insertRoomAdmin(roomAdmin) > 0;
    }

    public boolean deleteRoomAdmin(long userId, long roomId) {
        return roomAdminMapper.deleteRoomAdmin(userId, roomId) > 0;
    }
}
