package com.ytyo.Feign;

import com.ytyo.Model.RoomDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient("LiveSpace")
public interface LiveSpaceFeign {

    @PostMapping("/getRoomInfos")
    List<RoomDetails.RoomInfo> getRoomInfos(@RequestBody List<Long> roomIds);

    @GetMapping("/getRoomInfo")
    RoomDetails.RoomInfo getRoomInfo(@RequestParam Long roomId);

    @PostMapping("/getRoomInfosByAnchorIds")
    List<RoomDetails.RoomInfo> getRoomInfosByAnchorIds(@RequestBody List<Long> anchorIds);

    @PostMapping("/getRooms")
    List<RoomDetails> getRooms(@RequestBody List<Long> roomIds);

    @GetMapping("/getRoom")
    RoomDetails getRoom(@RequestParam Long roomId);

    /**
     * Long anchorId, Long userId
     */
    @PostMapping(value = "/addRoomAdmin", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String addRoomAdmin(Map<String, ?> formParamsMap);


    /**
     * Long anchorId, Long userId
     */
    @PostMapping(value = "/deleteRoomAdmin", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String deleteRoomAdmin(Map<String, ?> formParamsMap);

    @GetMapping("/getRoomAdminIds")
    List<Long> getRoomAdminIds(@RequestParam long anchorId);


}