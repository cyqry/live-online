package com.ytyo.Service;

import com.ytyo.Dao.WatchHistoryMapper;
import com.ytyo.Model.WatchHistory;
import com.ytyo.Option.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WatchHistoryService {
    @Autowired
    WatchHistoryMapper historyMapper;

    public boolean saveHistory(Long roomId, Long userId) {
        if (roomId == null || userId == null) {
            return false;
        }
        return historyMapper.insertWatchHistory(new WatchHistory(userId, roomId, LocalDateTime.now())) > 0;
    }

    public boolean updateHistory(Long roomId, Long userId) {
        if (roomId == null || userId == null) {
            return false;
        }
        return historyMapper.updateWatchHistoryLastWatchTime(new WatchHistory(userId, roomId, LocalDateTime.now())) > 0;
    }

    public Option<List<WatchHistory>> getUserWatchHistory(Long userId) {
        if (userId == null) {
            return Option.None();
        }
        return Option.from(historyMapper.selectWatchHistoryByUserId(userId));
    }

    public boolean updateOrPutHistory(Long roomId, Long userId) {
        if (roomId == null || userId == null) {
            return false;
        }
        return historyMapper.updateOrInsertHistory(new WatchHistory(userId, roomId, LocalDateTime.now())) > 0;
    }

    public boolean deleteHistory(Long roomId, Long userId) {
        if (roomId == null || userId == null) {
            return false;
        }
        return historyMapper.deleteWatchHistory(roomId, userId) > 0;
    }
}
