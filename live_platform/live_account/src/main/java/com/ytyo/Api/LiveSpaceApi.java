package com.ytyo.Api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ytyo.Feign.LiveSpaceFeign;
import com.ytyo.Model.RoomDetails;
import com.ytyo.Option.Option;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class LiveSpaceApi {

    @Autowired
    LiveSpaceFeign liveSpaceFeign;

    @Autowired
    ObjectMapper objectMapper;


    public Option<List<RoomDetails.RoomInfo>> getRoomInfos(Long... roomIds) {
        if (roomIds == null) {
            return Option.None();
        }
        if (roomIds.length == 0) {
            return Option.Some(new ArrayList<>());
        }

        try {
            return Option.from(liveSpaceFeign.getRoomInfos(List.of(roomIds)));
        } catch (FeignException e) {
            return Option.None();
        }


//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(RemoteConst.REMOTE_INVOKE_HEADER, RemoteConst.REMOTE_INVOKE_TOKEN);
//
//        try {
//            String url = "http://localhost:8081/getRoomInfos";
//
//            HttpEntity<List<Long>> requestEntity = new HttpEntity<>(Stream.of(roomIds).toList(), headers);
//
//            ResponseEntity<List<RoomDetails.RoomInfo>> result = restTemplate.exchange(url, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {
//            });
//
//            if (result.getStatusCode() == HttpStatus.OK) {
//                System.out.println(result.getBody());
//                return Option.from(result.getBody());
//            } else {
//                System.out.println(result.getStatusCode());
//                System.out.println(result.getBody());
//                return Option.None();
//            }
//        } catch (RestClientException e) {
//            e.printStackTrace();
//            return Option.None();
//        }
    }

    public Option<RoomDetails.RoomInfo> getRoomInfo(long roomId) {


        RoomDetails.RoomInfo info = liveSpaceFeign.getRoomInfo(roomId);
        try {
            return Option.from(info);
        } catch (FeignException e) {
            return Option.None();
        }
//        try {
//
//            RestTemplate restTemplate = new RestTemplate();
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//            headers.set(RemoteConst.REMOTE_INVOKE_HEADER, invokeToken);
//            String url = "http://localhost:8081/getRoomInfo?roomId=" + roomId;
//
//            ResponseEntity<RoomDetails.RoomInfo> roomInfo = restTemplate.exchange(
//                    url,
//                    HttpMethod.GET,
//                    new HttpEntity<String>(headers),
//                    RoomDetails.RoomInfo.class
//            );
////
////            if (roomInfo.getStatusCode() == HttpStatus.OK) {
////                System.out.println(roomInfo.getBody());
////                return Option.from(roomInfo.getBody());
////            } else {
////                System.out.println(roomInfo.getStatusCode());
////                System.out.println(roomInfo.getBody());
////                // handle error
////                return Option.None();
////            }
////        } catch (RestClientException e) {
////            e.printStackTrace();
////            return Option.None();
////        }
    }


    public Option<List<RoomDetails.RoomInfo>> getRoomInfosByAnchorIds(Long... anchorIds) {
        if (anchorIds == null) {
            return Option.None();
        }
        if (anchorIds.length == 0)
            return Option.Some(new ArrayList<>());
        try {
            List<RoomDetails.RoomInfo> infos = liveSpaceFeign.getRoomInfosByAnchorIds(List.of(anchorIds));
            return Option.from(infos);
        } catch (FeignException e) {
            return Option.None();
        }
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(RemoteConst.REMOTE_INVOKE_HEADER, RemoteConst.REMOTE_INVOKE_TOKEN);
//
//        try {
//            String url = "http://localhost:8081/getRoomInfosByAnchorIds";
//
//            HttpEntity<List<Long>> requestEntity = new HttpEntity<>(Stream.of(anchorIds).toList(), headers);
//
//            ResponseEntity<List<RoomDetails.RoomInfo>> result = restTemplate.exchange(url, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {
//            });
//
//            if (result.getStatusCode() == HttpStatus.OK) {
//                System.out.println(result.getBody());
//                return Option.from(result.getBody());
//            } else {
//                System.out.println(result.getStatusCode());
//                System.out.println(result.getBody());
//                return Option.None();
//            }
//        } catch (RestClientException e) {
//            e.printStackTrace();
//            return Option.None();
//        }
    }

    public Option<List<RoomDetails>> getRoomByRoomIds(Long... roomIds) {
        if (roomIds == null) {
            return Option.None();
        }
        if (roomIds.length == 0) {
            return Option.Some(new ArrayList<>());
        }

        try {
            List<RoomDetails> rooms = liveSpaceFeign.getRooms(List.of(roomIds));
            return Option.from(rooms);
        } catch (FeignException e) {
            return Option.None();
        }
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(RemoteConst.REMOTE_INVOKE_HEADER, RemoteConst.REMOTE_INVOKE_TOKEN);
//
//        try {
//            String url = "http://localhost:8081/getRooms";
//
//            HttpEntity<List<Long>> requestEntity = new HttpEntity<>(Stream.of(roomIds).toList(), headers);
//
//            ResponseEntity<List<RoomDetails>> result = restTemplate.exchange(url, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {
//            });
//
//            if (result.getStatusCode() == HttpStatus.OK) {
//                System.out.println(result.getBody());
//                return Option.from(result.getBody());
//            } else {
//                System.out.println(result.getStatusCode());
//                System.out.println(result.getBody());
//                return Option.None();
//            }
//        } catch (RestClientException e) {
//            e.printStackTrace();
//            return Option.None();
//        }
    }

    public Option<RoomDetails> getRoom(Long roomId) {
        if (roomId == null) {
            return Option.None();
        }
        try {
            RoomDetails room = liveSpaceFeign.getRoom(roomId);
            return Option.from(room);
        } catch (FeignException e) {
            return Option.None();
        }
//        String invokeToken = RemoteConst.REMOTE_INVOKE_TOKEN;
//        try {
//
//            RestTemplate restTemplate = new RestTemplate();
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//            headers.set(RemoteConst.REMOTE_INVOKE_HEADER, invokeToken);
//            String url = "http://localhost:8081/getRoom?roomId=" + roomId;
//
//            ResponseEntity<RoomDetails> roomDetails = restTemplate.exchange(
//                    url,
//                    HttpMethod.GET,
//                    new HttpEntity<String>(headers),
//                    RoomDetails.class
//            );
//
//            if (roomDetails.getStatusCode() == HttpStatus.OK) {
//                System.out.println(roomDetails.getBody());
//                return Option.from(roomDetails.getBody());
//            } else {
//                System.out.println(roomDetails.getStatusCode());
//                System.out.println(roomDetails.getBody());
//                return Option.None();
//            }
//        } catch (RestClientException e) {
//            e.printStackTrace();
//            return Option.None();
//        }
    }

    public Option<List<Long>> getRoomAdminIds(Long anchorId) {
        if (anchorId == null) {
            return Option.Some(new ArrayList<>());
        }

        return Option.from(liveSpaceFeign.getRoomAdminIds(anchorId));

//        String invokeToken = RemoteConst.REMOTE_INVOKE_TOKEN;
//        try {
//            RestTemplate restTemplate = new RestTemplate();
//            HttpHeaders headers = new HttpHeaders();
//            headers.set(RemoteConst.REMOTE_INVOKE_HEADER, invokeToken);
//
//            HttpEntity<String> entity = new HttpEntity<>(headers);
//
//            String url = "http://localhost:8081/getRoomAdminIds";
//
//            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
//                    .queryParam("anchorId", anchorId);
//
//            // 发起 GET 请求并获取响应
//            ResponseEntity<Long[]> response = restTemplate.exchange(
//                    builder.toUriString(),
//                    HttpMethod.GET,
//                    entity,
//                    Long[].class
//            );
//
//            if (response.getBody() == null) {
//                return Option.None();
//            }
//
//            // 从响应中提取结果
//            Long[] resultArray = response.getBody();
//            return Option.Some(List.of(resultArray));
//        } catch (RestClientException e) {
//            e.printStackTrace();
//            return Option.None();
//        }
    }

    public boolean addRoomAdmin(Long anchorId, Long userId) {
        if (anchorId == null || userId == null) {
            return false;
        }

        try {
            return StringUtils.hasText(liveSpaceFeign.addRoomAdmin(Map.of("anchorId", anchorId, "userId", userId)));
        } catch (FeignException e) {
            return false;
        }

//        String invokeToken = RemoteConst.REMOTE_INVOKE_TOKEN;
//        try {
//            RestTemplate restTemplate = new RestTemplate();
//            HttpHeaders headers = new HttpHeaders();
//            headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
//            headers.set(RemoteConst.REMOTE_INVOKE_HEADER, invokeToken);
//            String url = "http://localhost:8081/addRoomAdmin";
//
//            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
//                    .queryParam("anchorId", anchorId)
//                    .queryParam("userId", userId);
//
//            HttpEntity<String> entity = new HttpEntity<>(headers);
//            // 发起 Post 请求并获取响应
//            ResponseEntity<String> response = restTemplate.exchange(
//                    builder.toUriString(),
//                    HttpMethod.POST,
//                    entity,
//                    String.class
//            );
//
//            return StringUtils.hasText(response.getBody());
//        } catch (RestClientException e) {
//            e.printStackTrace();
//            return false;
//        }
    }

    public boolean deleteRoomAdmin(Long anchorId, Long userId) {
        if (anchorId == null || userId == null) {
            return false;
        }

        try {
            return StringUtils.hasText(liveSpaceFeign.deleteRoomAdmin(Map.of("anchorId", anchorId, "userId", userId)));
        } catch (FeignException e) {
            return false;
        }
//        String invokeToken = RemoteConst.REMOTE_INVOKE_TOKEN;
//        try {
//            RestTemplate restTemplate = new RestTemplate();
//            HttpHeaders headers = new HttpHeaders();
//            headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
//            headers.set(RemoteConst.REMOTE_INVOKE_HEADER, invokeToken);
//            String url = "http://localhost:8081/deleteRoomAdmin";
//
//            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
//                    .queryParam("anchorId", anchorId)
//                    .queryParam("userId", userId);
//
//            HttpEntity<String> entity = new HttpEntity<>(headers);
//            // 发起 GET 请求并获取响应
//            ResponseEntity<String> response = restTemplate.exchange(
//                    builder.toUriString(),
//                    HttpMethod.POST,
//                    entity,
//                    String.class
//            );
//
//            return StringUtils.hasText(response.getBody());
//        } catch (RestClientException e) {
//            e.printStackTrace();
//            return false;
//        }
    }
}
