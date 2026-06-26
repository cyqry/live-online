package com.ytyo.Feign;

import com.ytyo.Config.FeignConfig;
import com.ytyo.Const.GeneralConst;
import com.ytyo.Model.RoomDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(value = "LiveSpace", configuration = FeignConfig.class)
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

    @PostMapping(value = "/updateLastLiveTime", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String updateLastLiveTime(Map<String, ?> formParamsMap, @RequestHeader(GeneralConst.X_USER_HEADER) String userToken);

    /**
     * Long roomId, Short who, Long id
     */
    @PostMapping(value = "/enterRoom", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String enterRoom(Map<String, ?> formParamsMap, @RequestHeader(GeneralConst.X_USER_HEADER) String userToken);

    @PostMapping(value = "/leaveRoom", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String leaveRoom(Map<String, ?> formParamsMap, @RequestHeader(GeneralConst.X_USER_HEADER) String userToken);

    @PostMapping(value = "/offlineRoom", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String offlineRoom(Map<String, ?> formParamsMap, @RequestHeader(GeneralConst.X_USER_HEADER) String userToken);
}