package com.ytyo.Model.ElasticSearch;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 房间信息的es部分
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomTag {

    Long roomId;
    String title;
    //一级类别名  如休闲,娱乐,游戏
//    String firstLevelCategoryName;  //房间信息存二级类别就够，不需要一级类别
    //二级类别名 如王者荣耀，绝地求生
    private String firstLevelCategoryName;
    String secondLevelCategoryName;
    //直播项 端游,手游
    String roomItemCategoryName;
}
