package com.ytyo.Manager;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.json.JsonData;
import com.ytyo.DataConst.EsConst;
import com.ytyo.Model.ElasticSearch.Category.AbstractCategory;
import com.ytyo.Model.ElasticSearch.Category.FirstLevelCategory;
import com.ytyo.Model.ElasticSearch.Category.RoomItemCategory;
import com.ytyo.Model.ElasticSearch.Category.SecondLevelCategory;
import com.ytyo.Model.ElasticSearch.FirstLevel;
import com.ytyo.Model.ElasticSearch.RoomItem;
import com.ytyo.Model.ElasticSearch.RoomTag;
import com.ytyo.Model.ElasticSearch.SecondLevel;
import com.ytyo.Option.Option;
import com.ytyo.Option.Result;
import com.ytyo.Utils.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static co.elastic.clients.elasticsearch._types.Result.Deleted;

@Component
public class ElasticSearchManager {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    ElasticsearchClient client;

    /**
     * 只能创建文档,否则异常,这里我们显示传入一下id,对象里面有id的话，会被建立一个额外的id字段
     */
    public Result<String, Exception> saveRoomTag(RoomTag roomTag, String id) {
        CreateResponse response;
        try {
            response = client.create(create -> create
                    .index(EsConst.ROOM_TAG_INDEX)
                    .id(id)
                    .document(roomTag)
            );
            if (response == null || response.id() == null)
                return Result.Err(new RuntimeException("响应或响应id为空"));
            return Result.Ok(response.id());
        } catch (Exception e) {
            return Result.Err(e);
        }
    }


    public <T extends AbstractCategory> Result<Boolean, Exception> deleteAllCategories(Class<T> clazz) {
        if (clazz == null)
            return Result.Err(new RuntimeException("传参错误"));
        String index;
        if (clazz == SecondLevelCategory.class) {
            index = EsConst.SECOND_LEVEL_INDEX;
        } else if (clazz == RoomItemCategory.class) {
            index = EsConst.ROOM_ITEM_LEVEL_INDEX;
        } else if (clazz == FirstLevelCategory.class) {
            index = EsConst.FIRST_LEVEL_INDEX;
        } else {
            return Result.Err(new RuntimeException("参数错误！！"));
        }
        try {
            DeleteByQueryResponse delete = client.deleteByQuery(d -> d
                    .index(index)
                    .query(q -> q
                            .matchAll(m -> m)
                    )
            );
            if (delete == null || delete.deleted() == null)
                return Result.Err(new RuntimeException("删除时错误的响应"));
            return Result.Ok(delete.deleted() > 0);
        } catch (IOException e) {
            return Result.Err(e);
        }

    }

    public Result<Boolean, Exception> deleteAllRoomTag() {
        try {
            DeleteByQueryResponse delete = client.deleteByQuery(d -> d
                    .index(EsConst.ROOM_TAG_INDEX)
                    .query(q -> q
                            .matchAll(m -> m)
                    )
            );
            if (delete == null || delete.deleted() == null)
                return Result.Err(new RuntimeException("删除时错误的响应"));
            return Result.Ok(delete.deleted() > 0);
        } catch (IOException e) {
            return Result.Err(e);
        }
    }


    public Result<Boolean, Exception> deleteRoomTagByRoomId(Long roomId) {
        if (roomId == null)
            return Result.Err(new RuntimeException("传参错误!"));
        try {
            DeleteByQueryResponse delete = client.deleteByQuery(d -> d
                    .index(EsConst.ROOM_TAG_INDEX)
                    .query(q -> q
                            .term(t -> t
                                    .field("roomId")
                                    .value(roomId)
                            )
                    )
            );
            if (delete == null || delete.deleted() == null)
                return Result.Err(new RuntimeException("删除时错误的响应"));
            return Result.Ok(delete.deleted() > 0);
        } catch (IOException e) {
            return Result.Err(e);
        }
    }

    /**
     * 底层是一个更新操作，虽然update不是put相当于删除原对象再插入新对象.只会根据 请求中的字段 更新或新增；但是这里请求的字段的值好像可以为null，导致了我们 本不希望覆盖的字段 的值被null覆盖
     * 但是这里传map，就只更新了更新条件的非空字段
     */
    public Result<Boolean, Exception> updateRoomTagByRoomId(RoomTag roomTag) {
        Map<String, Object> updateMap = BeanUtil.extractNonNullFields(roomTag);

        if (roomTag == null || roomTag.getRoomId() == null || updateMap.size() == 0) {
            return Result.Err(new RuntimeException("参数错误!"));
        }

        Long roomId = roomTag.getRoomId();
        StringBuilder script = new StringBuilder();
        updateMap.forEach((key, value) -> {
            if (!"roomId".equals(key))
                script.append(String.format("ctx._source.%s='%s';", key, value));
        });
        script.delete(script.length() - 1, script.length());

        try {
            UpdateByQueryResponse response = client.updateByQuery(update -> update
                    .index(EsConst.ROOM_TAG_INDEX)
                    .query(q -> q
                            .term(t -> t
                                    .field("roomId")
                                    .value(roomId)
                            )
                    )
                    .script(s -> s
                            .inline(inline -> inline
                                    .source(script.toString())
                            )
                    )
            );

            return Result.Ok(response != null && response.updated() != null && response.updated() > 0);
        } catch (Exception e) {
            return Result.Err(e);
        }
    }


    public Result<Boolean, Exception> createIndex(String name) {
        try {
            CreateIndexResponse createResp = client.indices().create(create -> create
                    .index(name)
                    .settings(settings -> settings
                            .index(index -> index
                                    .otherSettings("analysis.analyzer.default.type", JsonData.of("ik_max_word"))
                            )
                            .numberOfShards("3")
                            .numberOfReplicas("2")
                    )
            );
            return Result.Ok(createResp != null);
        } catch (IOException | ElasticsearchException e) {
            return Result.Err(e);
        }
    }


    public Result<RoomItemCategory, Exception> findRoomItemCategoryById(String id) {
        if (id == null) {
            return Result.Err(new RuntimeException("findRoomItemCategoryById id为空!"));
        }
        try {
            SearchResponse<RoomItemCategory> response = client.search(s -> s
                            .index(EsConst.ROOM_ITEM_LEVEL_INDEX)
                            .query(q -> q
                                    .ids(ids -> ids
                                            .values(id)
                                    )
                            )
                    , RoomItemCategory.class);

            List<Hit<RoomItemCategory>> hits = response.hits().hits();
            if (hits.isEmpty() || hits.get(0) == null || hits.get(0).source() == null)
                return Result.Err(new RuntimeException("未找到RoomItemCategory"));
            return Result.Ok(hits.get(0).source());
        } catch (Exception e) {
            return Result.Err(e);
        }
    }


//    public Result<SecondLevelCategory, Exception> findSecondLevelCategoryById(String id) {
//        if (id == null) {
//            return Result.Err(new RuntimeException("findSecondLevelCategoryById id为空!"));
//        }
//        try {
//            SearchResponse<SecondLevelCategory> response = client.search(s -> s
//                            .index(Const.SECOND_LEVEL_INDEX)
//                            .query(q -> q
//                                    .ids(ids -> ids
//                                            .values(id)
//                                    )
//                            )
//                    , SecondLevelCategory.class);
//            List<Hit<SecondLevelCategory>> hits = response.hits().hits();
//            if (hits.isEmpty() || hits.get(0) == null || hits.get(0).source() == null) {
//                return Result.Err(new RuntimeException("未找到SecondLevelCategory"));
//            }
//            return Result.Ok(hits.get(0).source());
//        } catch (Exception e) {
//            return Result.Err(e);
//        }
//    }


    /**
     * 索引名错了就报错
     */
    public SearchResponse<Object> findAll(String... indices) {

        try {
            return client.search(s -> s.index(List.of(indices))
                            .query(q -> q
                                    .matchAll(builder -> builder)
                            )
                    , Object.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Result<RoomTag, Exception> findRoomTagByRoomId(Long roomId) {
        if (roomId == null) {
            return Result.Err(new RuntimeException("参数错误"));
        }
        try {
            SearchResponse<RoomTag> response = client.search(s -> s
                            //入参形式这样设计是因为要传一个或多个参数
                            .index(EsConst.ROOM_TAG_INDEX)
                            .query(query -> query
                                    .term(term -> term
                                            .field("roomId")
                                            .value(roomId)
                                    )
                            )
                    , RoomTag.class);
            if (response == null || response.hits() == null || response.hits().hits() == null || response.hits().hits().isEmpty() || response.hits().hits().get(0).source() == null) {
                return Result.Err(new RuntimeException("未找到!"));
            }
            return Result.Ok(response.hits().hits().get(0).source());
        } catch (IOException | ElasticsearchException e) {
            return Result.Err(e);
        }
    }

    public Result<SecondLevelCategory, Exception> findSecondLevelCategoryById(String id) {
        try {
            if (id == null)
                return Result.Err(new RuntimeException("参数错误！"));
            GetResponse<SecondLevelCategory> secondLevel = client.get(GetRequest.of(reqBuilder -> reqBuilder
                            .index(EsConst.SECOND_LEVEL_INDEX)
                            .id(id)
                    ),
                    SecondLevelCategory.class
            );
            if (secondLevel == null || secondLevel.source() == null) {
                return Result.Err(new RuntimeException("未找到SecondLevelCategory!"));
            }
            return Result.Ok(secondLevel.source());
        } catch (Exception e) {
            return Result.Err(e);
        }
    }

    public Result<List<RoomTag>, Exception> findRoomTagByName(String secondLevelName, String roomItemName, int pageSize) {

        if (secondLevelName == null || roomItemName == null) {
            return Result.Err(new RuntimeException("参数错误"));
        }

        try {
            SearchResponse<RoomTag> response = client.search(s -> s
                            .index(EsConst.ROOM_TAG_INDEX)
                            .query(q -> q
                                    .bool(b -> b
                                            .must(m -> m
                                                    .term(t -> t
                                                            .field("secondLevelCategoryName.keyword")
                                                            .value(secondLevelName)
                                                    )
                                            )
                                            .must(m -> m
                                                    .term(t -> t
                                                            .field("roomItemCategoryName.keyword")
                                                            .value(roomItemName)
                                                    )
                                            )
                                    )
                            )
                            .size(pageSize == 0 ? 10 : pageSize)
                    , RoomTag.class);
            if (response == null || response.hits() == null || response.hits().hits() == null) {
                return Result.Err(new RuntimeException("查询异常"));
            }
            ArrayList<RoomTag> result = new ArrayList<>();
            for (Hit<RoomTag> hit : response.hits().hits()) {
                if (hit != null && hit.source() != null)
                    result.add(hit.source());
            }
            return Result.Ok(result);
        } catch (IOException e) {
            return Result.Err(e);
        }
    }

    public <T> Result<Boolean, Exception> deleteCategory(String id, Class<T> clazz) {
        if (id == null || clazz == null)
            return Result.Err(new RuntimeException("传参错误"));
        String index;
        if (clazz.equals(SecondLevelCategory.class)) {
            index = EsConst.SECOND_LEVEL_INDEX;
        } else if (clazz.equals(RoomItemCategory.class)) {
            index = EsConst.ROOM_ITEM_LEVEL_INDEX;
        } else if (clazz.equals(FirstLevelCategory.class)) {
            index = EsConst.FIRST_LEVEL_INDEX;
        } else {
            return Result.Err(new RuntimeException("参数错误！！"));
        }
        try {
            DeleteResponse response = client.delete(DeleteRequest.of(r -> r
                    .index(index)
                    .id(id)
            ));
            System.out.println(response);
            return Result.Ok(response != null && Deleted.equals(response.result()));//未找到的result是"not_found“
        } catch (IOException e) {
            return Result.Err(e);
        }
    }

    public <T, E extends T> Result<List<E>, Exception> findAllCategoryByParentId(String parentId, Class<T> clazz, Comparator<? super Hit<T>> c) {
        if (parentId == null || clazz == null)
            return Result.Err(new RuntimeException("传参错误"));
        String index;
        String parentIdName;
        Class<? extends T> clazzObj;
        if (clazz == SecondLevelCategory.class) {
            index = EsConst.SECOND_LEVEL_INDEX;
            parentIdName = "firstLevelId";
            clazzObj = (Class<? extends T>) SecondLevel.class;

        } else if (clazz == RoomItemCategory.class) {
            index = EsConst.ROOM_ITEM_LEVEL_INDEX;
            parentIdName = "secondLevelId";
            clazzObj = (Class<? extends T>) RoomItem.class;
        } else {
            return Result.Err(new RuntimeException("参数错误！！"));
        }

        try {
            SearchResponse<T> response = client.search(s -> s
                            .index(index)
                            .query(q -> q
                                    .term(t -> t
                                            .field(parentIdName)
                                            .value(parentId)
                                    )
                            )
                    , clazz);

            if (response == null || response.hits() == null || response.hits().hits() == null || response.hits().hits().isEmpty())
                return Result.Err(new RuntimeException("一个Category都没找到"));

            List<Hit<T>> hits = response.hits().hits();
            if (c != null)
                hits.sort(null);

            ArrayList<E> result = new ArrayList<>();
            Constructor<E> constructor = (Constructor<E>) clazzObj.getConstructor(String.class, clazz);

            hits.forEach(h -> {
                try {
                    result.add(constructor.newInstance(h.id(), h.source()));
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });
            return Result.Ok(result);
        } catch (IOException | NoSuchMethodException e) {
            return Result.Err(e);
        }
    }

    public <T extends AbstractCategory, E extends T> Result<List<E>, Exception> findAllCategory(Class<T> clazz, Comparator<? super Hit<T>> c) {
        return findCategories(clazz, -1, c);
    }

    public <T extends AbstractCategory, E extends T> Result<List<E>, Exception> findCategories(Class<T> clazz, int pageSize, Comparator<? super Hit<T>> c) {
        if (clazz == null || pageSize < -1)
            return Result.Err(new RuntimeException("传参错误"));
        String index;
        Class<? extends T> clazzObj;
        if (clazz == SecondLevelCategory.class) {
            index = EsConst.SECOND_LEVEL_INDEX;
            clazzObj = (Class<? extends T>) SecondLevel.class;
        } else if (clazz == RoomItemCategory.class) {
            index = EsConst.ROOM_ITEM_LEVEL_INDEX;
            clazzObj = (Class<? extends T>) RoomItem.class;
        } else if (clazz == FirstLevelCategory.class) {
            index = EsConst.FIRST_LEVEL_INDEX;
            clazzObj = (Class<? extends T>) FirstLevel.class;
        } else {
            return Result.Err(new RuntimeException("参数错误！！"));
        }

        try {
            SearchResponse<T> response;
            if (pageSize == -1) {
                response = client.search(s -> s
                                .index(index)
                                .query(q -> q
                                        .matchAll(a -> a)
                                )
                                .size(30)
                        , clazz);

            } else {
                response = client.search(s -> s
                                .index(index)
                                .query(q -> q
                                        .matchAll(a -> a)
                                )
                                .size(pageSize == 0 ? 10 : pageSize)
                        , clazz);
            }

            if (response == null || response.hits() == null || response.hits().hits() == null || response.hits().hits().isEmpty())
                return Result.Err(new RuntimeException("没有这类Category"));
            List<Hit<T>> hits = response.hits().hits();
            if (c != null)
                hits.sort(c);
            ArrayList<E> result = new ArrayList<>();

            Constructor<E> constructor = (Constructor<E>) clazzObj.getConstructor(String.class, clazz);

            hits.forEach(h -> {
                try {
                    result.add(constructor.newInstance(h.id(), h.source()));
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });
            return Result.Ok(result);
        } catch (IOException | NoSuchMethodException e) {
            return Result.Err(e);
        }

    }


    public Result<String, Exception> saveCategory(AbstractCategory category, String id) {
        CreateResponse response;

        if (category == null || id == null) {
            return Result.Err(new RuntimeException("参数错误！！"));
        }

        String index;

        switch (category) {
            case FirstLevelCategory firstLevelCategory -> {
                index = EsConst.FIRST_LEVEL_INDEX;
            }
            case SecondLevelCategory secondLevelCategory -> {
                index = EsConst.SECOND_LEVEL_INDEX;
            }
            case RoomItemCategory roomItemCategory -> {
                index = EsConst.ROOM_ITEM_LEVEL_INDEX;
            }
            default -> {
                return Result.Err(new RuntimeException("参数错误!"));
            }
        }

        try {
            response = client.create(create -> create
                    .index(index)
                    .id(id)
                    .document(category)
            );
            if (response == null || response.id() == null)
                return Result.Err(new RuntimeException("响应或响应id为空"));
            return Result.Ok(response.id());
        } catch (Exception e) {
            return Result.Err(e);
        }
    }


    public <T> Result<T, Exception> findCategoryById(String id, Class<T> clazz) {
        try {
            if (id == null || clazz == null)
                return Result.Err(new RuntimeException("参数错误！"));
            String index;
            if (clazz.equals(SecondLevelCategory.class)) {
                index = EsConst.SECOND_LEVEL_INDEX;
            } else if (clazz.equals(RoomItemCategory.class)) {
                index = EsConst.ROOM_ITEM_LEVEL_INDEX;
            } else if (clazz.equals(FirstLevelCategory.class)) {
                index = EsConst.FIRST_LEVEL_INDEX;
            } else {
                return Result.Err(new RuntimeException("参数错误！！"));
            }

            GetResponse<T> level = client.get(GetRequest.of(reqBuilder -> reqBuilder
                            .index(index)
                            .id(id)
                    ),
                    clazz
            );
            if (level == null || level.source() == null) {
                return Result.Err(new RuntimeException("未找到对应Category!"));
            }
            return Result.Ok(level.source());
        } catch (Exception e) {
            return Result.Err(e);
        }
    }

    public Result<List<RoomTag>, Exception> findRoomTag(String secondCategoryName, String roomItemCategoryName) {
        if (secondCategoryName == null || roomItemCategoryName == null) {
            return Result.Err(new RuntimeException("参数错误!"));
        }

        try {
            SearchResponse<RoomTag> response = client.search(s -> s.index(EsConst.ROOM_TAG_INDEX)
                            .query(q -> q
                                    .bool(b -> b
                                            .must(m -> m
                                                    .term(t -> t
                                                            .field("secondLevelCategoryName.keyword")
                                                            .value(secondCategoryName)
                                                    )
                                            )
                                            .must(m -> m
                                                    .term(t -> t
                                                            .field("roomItemCategoryName.keyword")
                                                            .value(roomItemCategoryName)
                                                    )
                                            )
                                    )
                            )
                    , RoomTag.class);
            if (response == null || response.hits() == null || response.hits().hits() == null) {
                return Result.Err(new RuntimeException("查询错误!"));
            }
            ArrayList<RoomTag> result = new ArrayList<>();
            for (Hit<RoomTag> hit : response.hits().hits()) {
                if (hit != null && hit.source() != null) {
                    result.add(hit.source());
                }
            }
            return Result.Ok(result);
        } catch (IOException e) {
            return Result.Err(e);
        }
    }


    public Result<List<RoomTag>, Exception> findRoomTag(String secondCategoryName) {
        if (secondCategoryName == null) {
            return Result.Err(new RuntimeException("参数错误!"));
        }
        try {
            SearchResponse<RoomTag> response = client.search(s -> s.index(EsConst.ROOM_TAG_INDEX)
                            .query(q -> q
                                    .bool(b -> b
                                            .must(m -> m
                                                    .term(t -> t
                                                            .field("secondLevelCategoryName.keyword")
                                                            .value(secondCategoryName)
                                                    )
                                            )
                                    )
                            )
                    , RoomTag.class);
            if (response == null || response.hits() == null || response.hits().hits() == null) {
                return Result.Err(new RuntimeException("查询错误!"));
            }
            ArrayList<RoomTag> result = new ArrayList<>();
            for (Hit<RoomTag> hit : response.hits().hits()) {
                if (hit != null && hit.source() != null) {
                    result.add(hit.source());
                }
            }
            return Result.Ok(result);
        } catch (IOException e) {
            return Result.Err(e);
        }
    }

    public Result<List<RoomTag>, Exception> findRoomTag(String condition, int pageSize) {
        if (condition == null) {
            return Result.Err(new RuntimeException("参数错误!"));
        }


        try {
            SearchResponse<RoomTag> response = client.search(s -> s.index(EsConst.ROOM_TAG_INDEX)
                            .query(query -> query
                                    .bool(bool -> bool
                                            .should(q -> q
                                                    .match(m -> m
                                                            .field("title")
                                                            .query(qu -> qu
                                                                    .stringValue(condition)
                                                            )
                                                            .minimumShouldMatch(String.valueOf(2))
                                                    )
                                            )
                                            .should(q -> q
                                                    .match(m -> m
                                                            .field("firstLevelCategoryName")
                                                            .query(qu -> qu
                                                                    .stringValue(condition)
                                                            )
                                                    )
                                            )
                                            .should(q -> q
                                                    .match(m -> m
                                                            .field("secondLevelCategoryName")
                                                            .query(qu -> qu
                                                                    .stringValue(condition)
                                                            )
                                                    )
                                            ).should(q -> q
                                                    .match(m -> m
                                                            .field("roomItemCategoryName")
                                                            .query(qu -> qu
                                                                    .stringValue(condition)
                                                            )
                                                    )
                                            )
                                    )

                            )
                            .highlight(h -> h
                                    .preTags("<em>")
                                    .postTags("</em>")
                                    .fields("title", hv -> hv)
                            )
                            .size(pageSize == 0 ? 10 : pageSize)
                    , RoomTag.class);
            if (response != null && response.hits() != null && response.hits().hits() != null) {
                ArrayList<RoomTag> list = new ArrayList<>();
                for (Hit<RoomTag> hit : response.hits().hits()) {
                    list.add(hit.source());
                }
                return Result.Ok(list);
            } else {
                return Result.Err(new RuntimeException("查询错误!"));
            }
        } catch (IOException e) {
            return Result.Err(e);
        }
    }


    //应该为result....
    public Option<List<RoomTag>> findRoomTagByRoomIds(Long... ids) {
        if (ids == null)
            return Option.None();
        if (ids.length == 0)
            return Option.Some(new ArrayList<>());

        try {
            SearchResponse<RoomTag> response = client.search(s -> s
                            .query(q -> q
                                    .terms(t -> t
                                            .field("roomId")
                                            .terms(ts -> ts
                                                    .value(Stream.of(ids).map(FieldValue::of).toList())
                                            )
                                    )
                            )
                    , RoomTag.class);
            if (response == null || response.hits() == null || response.hits().hits() == null) {
                return Option.None();
            }
            ArrayList<RoomTag> result = new ArrayList<>();
            response.hits().hits().forEach(
                    hit -> {
                        result.add(hit.source());
                    }
            );
            return Option.Some(result);
        } catch (IOException e) {
            return Option.None();
        }
    }

    public Result<List<RoomTag>, Exception> findAllRoomTagByPageSize(int pageSize) {
        try {
            SearchResponse<RoomTag> response = client.search(s -> s
                            .index(EsConst.ROOM_TAG_INDEX)
                            .query(q -> q
                                    .matchAll(a -> a)
                            )
                            .size(pageSize)
                    , RoomTag.class);
            if (response != null && response.hits() != null && response.hits().hits() != null) {
                ArrayList<RoomTag> list = new ArrayList<>();
                response.hits().hits().forEach(h -> list.add(h.source()));
                return Result.Ok(list);
            } else {
                return Result.Err(new RuntimeException("查询错误"));
            }
        } catch (IOException e) {
            return Result.Err(e);
        }
    }
}
