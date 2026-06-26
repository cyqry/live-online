package com.ytyo.Model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户观看历史记录实体
 */
@Data
@NoArgsConstructor
public class WatchHistory {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 房间id
     */
    private Long roomId;

    private LocalDateTime lastWatchTime;

    public WatchHistory(Long userId, Long roomId, LocalDateTime lastWatchTime) {
        this.userId = userId;
        this.roomId = roomId;
        this.lastWatchTime = lastWatchTime;
    }

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
