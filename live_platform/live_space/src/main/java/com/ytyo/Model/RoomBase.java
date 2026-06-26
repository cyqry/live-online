package com.ytyo.Model;

import com.ytyo.Model.ElasticSearch.RoomTag;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class RoomBase {

    private Long roomId;
    /**
     * 存客户端进入主播直播间的路径,而不是开播路径
     */

    private String title;

    private String firstLevelCategoryName;
    private String secondLevelCategoryName;

    private String roomItemCategoryName;

    private LocalDateTime lastLiveTime;

    public RoomBase(RoomDetails.RoomInfo roomInfo, RoomTag roomTag) {
        this.lastLiveTime = roomInfo.getLastLiveTime();
        this.roomId = roomInfo.getId();
        this.title = roomTag.getTitle();
        this.roomItemCategoryName = roomTag.getRoomItemCategoryName();
        this.secondLevelCategoryName = roomTag.getSecondLevelCategoryName();
        this.firstLevelCategoryName = roomTag.getFirstLevelCategoryName();
    }
}
