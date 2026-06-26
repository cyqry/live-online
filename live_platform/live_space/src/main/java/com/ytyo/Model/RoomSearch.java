package com.ytyo.Model;

import com.ytyo.Model.ElasticSearch.RoomTag;
import lombok.Data;

@Data
public class RoomSearch {
    Long roomId;
    String firstLevelCategoryName;
    String secondLevelCategoryName;
    //直播项 端游,手游
    String roomItemCategoryName;
    String title;

    String coverSrc;
    long hot;
    String anchorNickname;
    String anchorAvatar;

    public RoomSearch() {
    }

    public RoomSearch(RoomTag roomTag) {
        if (roomTag == null)
            throw new IllegalArgumentException("参数错误!");
        this.roomId = roomTag.getRoomId();
        this.firstLevelCategoryName = roomTag.getFirstLevelCategoryName();
        this.secondLevelCategoryName = roomTag.getSecondLevelCategoryName();
        this.roomItemCategoryName = roomTag.getRoomItemCategoryName();
        this.title = roomTag.getTitle();
    }

    public RoomSearch(RoomDetails roomDetails) {
        if (roomDetails == null)
            throw new IllegalArgumentException("参数错误!");
        this.roomId = roomDetails.getRoomInfo().getId();
        this.coverSrc = roomDetails.getCoverSrc();
        this.hot = roomDetails.getUserList().size() + roomDetails.getVisitorList().size() + this.hot;
    }

    public RoomSearch(User anchor) {
        if (anchor == null)
            throw new IllegalArgumentException("参数错误!");
        this.anchorNickname = anchor.getNickname();
        this.anchorAvatar = anchor.getAvatar();
    }

    public void attach(RoomTag roomTag) {
        if (roomTag == null)
            throw new IllegalArgumentException("参数错误!");
        this.roomId = roomTag.getRoomId();
        this.firstLevelCategoryName = roomTag.getFirstLevelCategoryName();
        this.secondLevelCategoryName = roomTag.getSecondLevelCategoryName();
        this.roomItemCategoryName = roomTag.getRoomItemCategoryName();
        this.title = roomTag.getTitle();
    }

    public void attach(RoomDetails roomDetails) {
        if (roomDetails == null)
            throw new IllegalArgumentException("参数错误!");
        if (this.roomId == null)
            this.roomId = roomDetails.getRoomInfo().getId();
        this.coverSrc = roomDetails.getCoverSrc();
        this.hot = roomDetails.getUserList().size() + roomDetails.getVisitorList().size() + this.hot;
    }

    public void attach(User anchor) {
        if (anchor == null)
            throw new IllegalArgumentException("参数错误!");
        this.anchorNickname = anchor.getNickname();
        this.anchorAvatar = anchor.getAvatar();
    }
}
