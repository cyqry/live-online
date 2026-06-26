package com.ytyo.Service;

import com.ytyo.Dao.RoomInfoMapper;
import com.ytyo.Manager.ElasticSearchManager;
import com.ytyo.Model.ElasticSearch.Category.FirstLevelCategory;
import com.ytyo.Model.ElasticSearch.Category.RoomItemCategory;
import com.ytyo.Model.ElasticSearch.Category.SecondLevelCategory;
import com.ytyo.Model.ElasticSearch.RoomTag;
import com.ytyo.Model.RoomDetails;
import com.ytyo.Option.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RoomBaseService {
    @Autowired
    ElasticSearchManager elasticSearchManager;
    @Autowired
    RoomInfoMapper roomInfoMapper;


    public boolean createRoomBase(Long anchorId, String roomItemCategoryId) throws Exception {
        if (anchorId == null || roomItemCategoryId == null)
            return false;


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

        //保存RoomInfo
        RoomDetails.RoomInfo roomInfo = new RoomDetails.RoomInfo();
        roomInfo.setAnchorId(anchorId);
        long rows = roomInfoMapper.insertRoomInfo(roomInfo);
        if (roomInfo.getId() == null || rows < 1)
            return false;

        //保存RoomTag
        RoomTag roomTag = new RoomTag();
        roomTag.setSecondLevelCategoryName(secondLevelCategory.unwrap().getName());
        roomTag.setRoomItemCategoryName(roomItemCategory.unwrap().getName());
        roomTag.setRoomId(roomInfo.getId());
        roomTag.setFirstLevelCategoryName(fistLevelCategory.unwrap().getName());
        elasticSearchManager.saveRoomTag(roomTag, UUID.randomUUID().toString()).unwrap();
        return true;
    }
}
