package com.ytyo.Model.ElasticSearch;

import com.ytyo.Model.ElasticSearch.Category.RoomItemCategory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 直播项
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class RoomItem extends RoomItemCategory {
    String id;

    public RoomItem(String id, RoomItemCategory roomItemCategory) {
        super(roomItemCategory.getName(), roomItemCategory.getSecondLevelId());
        this.id = id;
    }
}
