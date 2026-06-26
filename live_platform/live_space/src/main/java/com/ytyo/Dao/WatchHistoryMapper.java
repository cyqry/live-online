package com.ytyo.Dao;

import com.ytyo.Model.WatchHistory;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface WatchHistoryMapper {
    List<WatchHistory> selectWatchHistoryByUserId(long userId);
    long insertWatchHistory(WatchHistory watchHistory);
    long updateWatchHistoryLastWatchTime(WatchHistory watchHistory);

    long deleteWatchHistory(long userId,long roomId);

    long updateOrInsertHistory(WatchHistory watchHistory);
}
