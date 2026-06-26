package com.ytyo.Model.ElasticSearch.Category;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 直播项
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString
public class RoomItemCategory extends AbstractCategory {
    public RoomItemCategory(String name, String secondLevelId) {
        this.name = name;
        this.secondLevelId = secondLevelId;
    }

    String secondLevelId;
}
