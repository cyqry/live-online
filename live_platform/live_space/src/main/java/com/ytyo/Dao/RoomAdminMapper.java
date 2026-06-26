package com.ytyo.Dao;

import com.ytyo.Model.RoomAdmin;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RoomAdminMapper {
    int insertRoomAdmin(RoomAdmin roomAdmin);

    int deleteRoomAdmin(long userId, long roomId);

    List<Long> selectUserIdsByRoomId(long roomId);

    List<RoomAdmin> selectRoomIdsByUserId(long userId);

    int countRoomAdmin(long userId, long roomId);
}
