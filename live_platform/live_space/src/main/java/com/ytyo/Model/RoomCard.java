package com.ytyo.Model;

import com.ytyo.Model.ElasticSearch.RoomTag;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 对应个人中心的RoomCard
 */
@Data
public class RoomCard {
    Long roomId;

    //直播最后一次直播时间,若为今天且online为true才显示在线
    LocalDateTime lastLiveTime;

    LocalDateTime lastWatchTime;

    //房间标题
    String liveTitle;

    //直播类型，这里是二级类名
    String domain;

    //主播是否在线
    boolean online;
    //主播昵称
    String anchorNickname;
    //主播头像path
    String avatarUrl;

    //关注人数
    long attentions;

    //关注+在线观看人数
    long hot;


    public void attach(User user) {
        if (user == null)
            throw new IllegalArgumentException("error");
        this.anchorNickname = user.getNickname();
        this.avatarUrl = user.getAvatar();
    }

    public void attach(WatchHistory watchHistory) {
        if (watchHistory == null)
            throw new IllegalArgumentException("error");
        this.lastWatchTime = watchHistory.getLastWatchTime();
        if (this.roomId == null) {
            this.roomId = watchHistory.getRoomId();
        }
    }

    public void attach(RoomDetails roomDetails) {
        if (roomDetails == null)
            throw new IllegalArgumentException("error");
        this.online = true;
        this.hot = this.hot + roomDetails.getUserList().size() + roomDetails.getVisitorList().size();
        if (this.lastLiveTime == null && roomDetails.getRoomInfo() != null) {
            this.lastLiveTime = roomDetails.getRoomInfo().getLastLiveTime();
        }
        if (this.roomId == null && roomDetails.getRoomInfo() != null) {
            this.roomId = roomDetails.getRoomInfo().getId();
        }
    }

    public void attach(RoomTag roomTag) {
        if (roomTag == null)
            throw new IllegalArgumentException("error");
        this.liveTitle = roomTag.getTitle();
        this.domain = roomTag.getSecondLevelCategoryName();
    }

    public void attach(RoomDetails.RoomInfo roomInfo) {
        if (roomInfo == null)
            throw new IllegalArgumentException("error");
        this.roomId = roomInfo.getId();
        this.lastLiveTime = roomInfo.getLastLiveTime();
    }
}
