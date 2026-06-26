package com.ytyo.Service;

import com.ytyo.Manager.ElasticSearchManager;
import com.ytyo.Model.ElasticSearch.Category.FirstLevelCategory;
import com.ytyo.Model.ElasticSearch.Category.RoomItemCategory;
import com.ytyo.Model.ElasticSearch.Category.SecondLevelCategory;
import com.ytyo.Utils.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class InitService {
    @Autowired
    ElasticSearchManager esManager;

    public boolean reInit(Map<String, Map<SecondLevelCategory, List<String>>> map) {
        if (map == null)
            return false;
        try {
            esManager.deleteAllCategories(FirstLevelCategory.class).unwrap();
            esManager.deleteAllCategories(SecondLevelCategory.class).unwrap();
            esManager.deleteAllCategories(RoomItemCategory.class).unwrap();
            AtomicInteger id = new AtomicInteger(1);
            map.forEach((first, sMap) -> {
                try {
                    FirstLevelCategory firstLevelCategory = new FirstLevelCategory();
                    firstLevelCategory.setName(first);
                    String firstId = esManager.saveCategory(firstLevelCategory, String.valueOf(id.getAndIncrement())).unwrap();
                    sMap.forEach((second, rList) -> {
                        try {
                            second.setFirstLevelId(firstId);
                            String secondId = esManager.saveCategory(second, String.valueOf(id.getAndIncrement())).unwrap();
                            rList.forEach(item -> {
                                try {
                                    RoomItemCategory roomItemCategory = new RoomItemCategory(item, secondId);
                                    String roomItemId = esManager.saveCategory(roomItemCategory, String.valueOf(id.getAndIncrement())).unwrap();
                                    System.out.println("firstId:" + firstId + ", secondId:" + secondId + ", roomItemId:" + roomItemId);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            log.info("reInit执行,{}", LogUtil.mapLog(map));
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
