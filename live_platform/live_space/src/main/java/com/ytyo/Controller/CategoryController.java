package com.ytyo.Controller;

import com.ytyo.Api.LiveAccountApi;
import com.ytyo.Model.*;
import com.ytyo.Model.ElasticSearch.Category.RoomItemCategory;
import com.ytyo.Model.ElasticSearch.Category.SecondLevelCategory;
import com.ytyo.Model.ElasticSearch.FirstLevel;
import com.ytyo.Model.ElasticSearch.RoomItem;
import com.ytyo.Model.ElasticSearch.RoomTag;
import com.ytyo.Model.ElasticSearch.SecondLevel;
import com.ytyo.Option.NoneException;
import com.ytyo.Option.Option;
import com.ytyo.Service.CategoryService;
import com.ytyo.Service.InitService;
import com.ytyo.Service.RoomDetailService;
import com.ytyo.Service.RoomTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/public")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @Autowired
    InitService initService;

    @Autowired
    RoomDetailService roomDetailService;
    @Autowired
    RoomTagService roomTagService;

    @Autowired
    LiveAccountApi liveAccountApi;

    @GetMapping("/getClassifyCategory")
    public ResponseEntity<?> getClassifyCategory() {

        List<ClassifyCategory> list = new ArrayList<>();
        try {
            List<FirstLevel> firstLevels = categoryService.getAllFirstLevel().unwrap();
            for (FirstLevel firstLevel : firstLevels) {
                Option<List<SecondLevel>> secondLevels = categoryService.getAllCategoryByParentId(firstLevel.getId(), SecondLevelCategory.class);
                list.add(new ClassifyCategory(firstLevel, secondLevels.unwrap()));
            }
            return ResponseEntity.ok(list);
        } catch (NoneException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("查询失败!");
        }

    }

    @GetMapping("/getAllCategories")
    public ResponseEntity<?> getAllCategory() {

        try {
            List<FirstLevel> firstLevels = categoryService.getAllFirstLevel().unwrap();
            List<SecondLevel> secondLevels = categoryService.getAllSecondLevel().unwrap();
            List<RoomItem> roomItems = categoryService.getAllRoomItem().unwrap();

//            for (FirstLevel firstLevel : firstLevels) {
//                List<LowLevelCategory> lowLevelList = new ArrayList<>();
//                Option<List<SecondLevel>> secondLevels = categoryService.getAllCategoryByParentId(firstLevel.getId(), SecondLevelCategory.class);
//                for (SecondLevel secondLevel : secondLevels.unwrap()) {
//                    Option<List<RoomItemCategory>> roomCategories = categoryService.getAllCategoryByParentId(secondLevel.getId(), RoomItemCategory.class);
//                    lowLevelList.add(new LowLevelCategory(secondLevel, roomCategories.unwrap()));
//                }
//                ;
//                allCategories.add(new AllCategory(firstLevel, lowLevelList));
//            }
            return ResponseEntity.ok(new AllCategory(firstLevels, secondLevels, roomItems));
        } catch (NoneException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("查询失败!");
        }
    }

    @GetMapping("/getRoomItems")
    public ResponseEntity<?> getRoomItems(String secondId) {
        if (secondId == null) {
            return ResponseEntity.badRequest().body("错误的请求");
        }

        try {
            Option<List<RoomItem>> level = categoryService.getAllCategoryByParentId(secondId, RoomItemCategory.class);
            return ResponseEntity.ok(level.unwrap());
        } catch (NoneException e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }


    /**
     * 该接口只能查3条数据
     * @param secondLevelName
     * @param roomItemName
     * @return
     */
    @GetMapping("/getRoomSearchsByName")
    public ResponseEntity<?> getRoomSearchsByName(String secondLevelName, String roomItemName) {
        if (secondLevelName == null || roomItemName == null) {
            return ResponseEntity.badRequest().body("错误的请求");
        }

        try {
            List<RoomTag> roomTags = roomTagService.selectRoomTagByName(secondLevelName.trim(), roomItemName.trim(), 3).unwrap();

            HashMap<Long, RoomSearch> map = new HashMap<>();

            for (RoomTag tag : roomTags) {
                map.put(tag.getRoomId(), new RoomSearch(tag));
            }

            List<RoomDetails> details = roomDetailService.getRoomDetailsByRoomIds(map.keySet().toArray(new Long[0])).unwrap();

            HashMap<Long, Long> anchorIdRoomIdMap = new HashMap<>();
            for (RoomDetails detail : details) {
                RoomSearch roomSearch = map.get(detail.getRoomInfo().getId());
                if (roomSearch != null) {
                    roomSearch.attach(detail);
                }
                anchorIdRoomIdMap.put(detail.getRoomInfo().getAnchorId(), detail.getRoomInfo().getId());
            }

            //剔除不在线的房间
            map.entrySet().removeIf((s) -> details.stream().noneMatch(d -> Objects.equals(d.getRoomInfo().getId(), s.getKey())));


            List<User> users = liveAccountApi.getUserByIds(anchorIdRoomIdMap.keySet().toArray(new Long[0])).unwrap();
            for (User user : users) {
                RoomSearch roomSearch = map.get(anchorIdRoomIdMap.get(user.getId()));
                if (roomSearch != null) {
                    roomSearch.attach(user);
                }
            }
            return ResponseEntity.ok(map.values());
        } catch (NoneException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("查询失败");
        }


    }

    @GetMapping("/getSecondLevels")
    public ResponseEntity<?> getSecondLevels(Integer pageSize) {
        if (pageSize == null || pageSize == 0) {
            pageSize = 10;
        }
        try {
            List<SecondLevel> levels = categoryService.getSecondLevels(pageSize).unwrap();
            return ResponseEntity.ok(levels);
        } catch (NoneException e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }


    @GetMapping("/selectSecondRoom")
    public ResponseEntity<?> getRoomSearchByItemId(String secondId) {
        if (secondId == null) {
            return ResponseEntity.badRequest().body("错误的请求");
        }
        if (!StringUtils.hasText(secondId)) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        Map<Long, RoomSearch> map = new HashMap<>();
        try {
            SecondLevelCategory secondLevelCategory = categoryService.getCategoryById(secondId, SecondLevelCategory.class).unwrap();
            List<RoomTag> roomTags = categoryService.getRoomTagsBySecondCategoryName(secondLevelCategory.getName()).unwrap();

            for (RoomTag tag : roomTags) {
                map.put(tag.getRoomId(), new RoomSearch(tag));
            }

            List<RoomDetails> details = roomDetailService.getRoomDetailsByRoomIds(map.keySet().toArray(new Long[0])).unwrap();
            HashMap<Long, Long> anchorIdRoomIdMap = new HashMap<>();
            for (RoomDetails detail : details) {
                RoomSearch roomSearch = map.get(detail.getRoomInfo().getId());
                if (roomSearch != null) {
                    roomSearch.attach(detail);
                }
                anchorIdRoomIdMap.put(detail.getRoomInfo().getAnchorId(), detail.getRoomInfo().getId());
            }

            //剔除不在线的房间
            map.entrySet().removeIf((s) -> details.stream().noneMatch(d -> Objects.equals(d.getRoomInfo().getId(), s.getKey())));


            List<User> users = liveAccountApi.getUserByIds(anchorIdRoomIdMap.keySet().toArray(new Long[0])).unwrap();
            for (User user : users) {
                RoomSearch roomSearch = map.get(anchorIdRoomIdMap.get(user.getId()));
                if (roomSearch != null) {
                    roomSearch.attach(user);
                }
            }
            return ResponseEntity.ok(map.values());
        } catch (NoneException e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
}
