package com.ytyo.Service;

import com.ytyo.Manager.ElasticSearchManager;
import com.ytyo.Model.ElasticSearch.Category.FirstLevelCategory;
import com.ytyo.Model.ElasticSearch.Category.RoomItemCategory;
import com.ytyo.Model.ElasticSearch.Category.SecondLevelCategory;
import com.ytyo.Model.ElasticSearch.FirstLevel;
import com.ytyo.Model.ElasticSearch.RoomItem;
import com.ytyo.Model.ElasticSearch.RoomTag;
import com.ytyo.Model.ElasticSearch.SecondLevel;
import com.ytyo.Option.Option;
import com.ytyo.Option.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CategoryService {
    @Autowired
    ElasticSearchManager elasticSearchManager;

    public <T, E extends T> Option<List<E>> getAllCategoryByParentId(String parentId, Class<T> clazz) {
        try {
            Result<List<E>, Exception> allCategory = elasticSearchManager.findAllCategoryByParentId(parentId, clazz, null);
            return Option.Some(allCategory.unwrap());
        } catch (Exception e) {
            log.error("getAllCategoryByParentId失败", e);
            return Option.None();
        }
    }

    public Option<List<RoomTag>> getRoomTagByCategoryName(String secondCategoryName, String roomItemCategoryName) {
        Result<List<RoomTag>, Exception> result = elasticSearchManager.findRoomTag(secondCategoryName, roomItemCategoryName);
        try {
            return Option.Some(result.unwrap());
        } catch (Exception e) {
            log.error("getRoomTagByCategoryName失败", e);
            return Option.None();
        }
    }

    public Option<List<RoomTag>> getRoomTagsBySecondCategoryName(String secondCategoryName) {
        Result<List<RoomTag>, Exception> result = elasticSearchManager.findRoomTag(secondCategoryName);
        try {
            return Option.Some(result.unwrap());
        } catch (Exception e) {
            log.error("getRoomTagsBySecondCategoryName失败", e);
            return Option.None();
        }
    }

    public <T> Option<T> getCategoryById(String id, Class<T> tClass) {
        try {
            Result<T, Exception> category = elasticSearchManager.findCategoryById(id, tClass);
            return Option.Some(category.unwrap());
        } catch (Exception e) {
            log.error("getCategoryById失败", e);
            return Option.None();
        }
    }

    public Option<List<FirstLevel>> getAllFirstLevel() {
        try {
            Result<List<FirstLevel>, Exception> allCategory = elasticSearchManager.findAllCategory(FirstLevelCategory.class, null);
            return Option.Some(allCategory.unwrap());
        } catch (Exception e) {
            log.error("getAllFirstLevel失败", e);
            return Option.None();
        }
    }

    public Option<List<RoomItem>> getAllRoomItem() {
        try {
            Result<List<RoomItem>, Exception> allCategory = elasticSearchManager.findAllCategory(RoomItemCategory.class, null);
            return Option.Some(allCategory.unwrap());
        } catch (Exception e) {
            log.error("getAllRoomItem失败", e);
            return Option.None();
        }
    }

    public Option<List<SecondLevel>> getAllSecondLevel() {
        try {
            Result<List<SecondLevel>, Exception> allCategory = elasticSearchManager.findAllCategory(SecondLevelCategory.class, null);
            return Option.Some(allCategory.unwrap());
        } catch (Exception e) {
            log.error("getAllSecondLevel失败", e);
            return Option.None();
        }
    }

    public Option<List<SecondLevel>> getSecondLevels(int pageSize) {
        try {
            Result<List<SecondLevel>, Exception> categories = elasticSearchManager.findCategories(SecondLevelCategory.class, pageSize, null);
            return Option.Some(categories.unwrap());
        } catch (Exception e) {
            log.error("getSecondLevels失败", e);
            return Option.None();
        }
    }
}
