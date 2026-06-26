package com.ytyo.Dao;

import com.ytyo.Model.UserFollowRoom;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface FollowMapper {

    List<UserFollowRoom> selectUserFollows(long userId, int pageSize);

    UserFollowRoom selectUserFollow(long userId, long roomId);

    List<UserFollowRoom> selectFollowRoomUsers(long roomId);

    long countRoomFollow(long roomId);

    @MapKey("room_id")
    Map<Long, Map<String,Long>> getFollowCountsByRoomIds(List<Long> roomIds);

    int countFollow(long userId, long roomId);

    long deleteFollow(long userId, long roomId);

    long insertFollow(long userId, long roomId);
}
