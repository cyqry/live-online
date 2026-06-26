package com.ytyo.Service;

import com.ytyo.Manager.ElasticSearchManager;
import com.ytyo.Model.ElasticSearch.Category.FirstLevelCategory;
import com.ytyo.Model.ElasticSearch.Category.RoomItemCategory;
import com.ytyo.Model.ElasticSearch.Category.SecondLevelCategory;
import com.ytyo.Model.ElasticSearch.RoomTag;
import com.ytyo.Option.Option;
import com.ytyo.Option.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class RoomTagService {

    @Autowired
    ElasticSearchManager elasticSearchManager;

    public boolean createRoomTag(String roomItemCategoryId, Long roomId) {
        if (roomItemCategoryId == null || roomId == null)
            return false;
        try {
            Result<RoomItemCategory, Exception> roomItemCategory = elasticSearchManager.findCategoryById(roomItemCategoryId, RoomItemCategory.class);
            if (roomItemCategory.isErr())
                return false;
            Result<SecondLevelCategory, Exception> secondLevelCategory = elasticSearchManager.findCategoryById(roomItemCategory.unwrap().getSecondLevelId(), SecondLevelCategory.class);
            if (secondLevelCategory.isErr()) {
                return false;
            }
            Result<FirstLevelCategory, Exception> fistLevelCategory = elasticSearchManager.findCategoryById(secondLevelCategory.unwrap().getFirstLevelId(), FirstLevelCategory.class);
            if (fistLevelCategory.isErr()) {
                return false;
            }

            RoomTag roomTag = new RoomTag();
            roomTag.setSecondLevelCategoryName(secondLevelCategory.unwrap().getName());
            roomTag.setRoomItemCategoryName(roomItemCategory.unwrap().getName());
            roomTag.setRoomId(roomId);
            roomTag.setFirstLevelCategoryName(fistLevelCategory.unwrap().getName());
            elasticSearchManager.saveRoomTag(roomTag, UUID.randomUUID().toString()).unwrap();
            return true;
        } catch (Exception e) {
            log.error("创建RoomTag失败", e);
            return false;
        }
    }


    public boolean updateTitle(String roomTitle, Long roomId) {
        RoomTag roomTag = new RoomTag();
        roomTag.setTitle(roomTitle);
        roomTag.setRoomId(roomId);
        try {
            return elasticSearchManager.updateRoomTagByRoomId(roomTag).unwrap();
        } catch (Exception e) {
            log.error("更新房间标题失败,roomTitle:{},roomId:{},error:{}", roomTitle, roomId, e);
            return false;
        }
    }

    public Option<List<RoomTag>> selectRoomTagByCondition(String condition, int pageSize) {
        if (condition == null)
            return Option.None();

        if (pageSize == 0)
            pageSize = 10;
        try {
            if (!StringUtils.hasText(condition)) {
                Result<List<RoomTag>, Exception> allRoomTagByPageSize = elasticSearchManager.findAllRoomTagByPageSize(pageSize);
                return Option.from(allRoomTagByPageSize.unwrap());
            } else {
                List<RoomTag> tags = elasticSearchManager.findRoomTag(condition, pageSize).unwrap();
                return Option.Some(tags);
            }
        } catch (Exception e) {
            log.error("selectRoomTagByCondition", e);
            return Option.None();
        }
    }

    public Option<List<RoomTag>> selectRoomTagByName(String secondLevelName, String roomItemName,int pageSize) {

        try {
            return Option.Some(elasticSearchManager.findRoomTagByName(secondLevelName, roomItemName,pageSize).unwrap());
        } catch (Exception e) {
            return Option.None();
        }

    }

    public Option<RoomTag> getRoomTagByRoomId(Long roomId) {
        try {
            RoomTag roomTag = elasticSearchManager.findRoomTagByRoomId(roomId).unwrap();
            return Option.Some(roomTag);
        } catch (Exception e) {
            log.error("getRoomTagByRoomId错误", e);
            return Option.None();
        }
    }

    public Option<List<RoomTag>> getRoomTagByRoomIds(Long... ids) {
        return elasticSearchManager.findRoomTagByRoomIds(ids);
    }
}
