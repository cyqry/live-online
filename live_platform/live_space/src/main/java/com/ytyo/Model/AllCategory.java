package com.ytyo.Model;

import com.ytyo.Model.ElasticSearch.FirstLevel;
import com.ytyo.Model.ElasticSearch.RoomItem;
import com.ytyo.Model.ElasticSearch.SecondLevel;

import java.util.List;

public record AllCategory(List<FirstLevel> firstLevels, List<SecondLevel> secondLevels,
                          List<RoomItem> roomItems) {
}


