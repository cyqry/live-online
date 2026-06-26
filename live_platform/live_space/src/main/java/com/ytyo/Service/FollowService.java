package com.ytyo.Service;

import com.ytyo.Dao.FollowMapper;
import com.ytyo.Model.UserFollowRoom;
import com.ytyo.Option.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FollowService {
    @Autowired
    FollowMapper followMapper;

    public Option<Long> countFollowRoom(Long roomId) {
        if (roomId == null) {
            return Option.None();
        }
        long count = followMapper.countRoomFollow(roomId);
        return Option.Some(count);
    }

    //用于判断是否关注
    public Option<UserFollowRoom> getFollow(Long userId, Long roomId) {
        if (userId == null || roomId == null) {
            return Option.None();
        }
        UserFollowRoom followRoom = followMapper.selectUserFollow(userId, roomId);
        return Option.from(followRoom);
    }

    public Option<List<UserFollowRoom>> getFollows(Long userId, int pageSize) {
        if (userId == null) {
            return Option.None();
        }
        List<UserFollowRoom> userFollowRooms = followMapper.selectUserFollows(userId, pageSize);
        return Option.from(userFollowRooms);
    }

    public boolean isFollow(long userId, long roomId) {
        return followMapper.countFollow(userId, roomId) > 0;
    }

    public Option<Map<Long, Long>> getFollowCounts(Long... roomIds) {
        if (roomIds == null) {
            return Option.None();
        }
        if (roomIds.length == 0) {
            return Option.Some(new HashMap<>());
        }
        Map<Long, Map<String, Long>> map = followMapper.getFollowCountsByRoomIds(List.of(roomIds));
        if (map == null)
            return Option.None();
        Map<Long, Long> result = new HashMap<>();
        map.forEach((roomId, valueMap) -> {
            Long count = valueMap.get("count");
            if (count != null)
                result.put(roomId, count);
        });
        return Option.Some(result);
    }

    public boolean subscribe(Long userId, Long roomId) {
        if (roomId == null || userId == null) {
            return false;
        }
        return followMapper.insertFollow(userId, roomId) > 0;
    }

    public boolean unsubscribe(Long userId, Long roomId) {
        if (roomId == null || userId == null) {
            return false;
        }
        return followMapper.deleteFollow(userId, roomId) > 0;
    }
}
