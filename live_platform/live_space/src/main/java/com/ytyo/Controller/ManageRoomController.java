package com.ytyo.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ytyo.annotation.authority.Remote;
import com.ytyo.annotation.authority.RemoteNoLoginRequired;
import com.ytyo.annotation.authority.SuperAdmin;
import com.ytyo.Api.LiveAccountApi;
import com.ytyo.Api.LiveStoreApi;
import com.ytyo.Api.LiveStreamingApi;
import com.ytyo.Model.*;
import com.ytyo.Model.Operating.OperatingRecord;
import com.ytyo.Model.Operating.OperatingRoom;
import com.ytyo.Model.Operating.OperationUser;
import com.ytyo.Option.NoneException;
import com.ytyo.Option.Option;
import com.ytyo.Service.OperatingService;
import com.ytyo.Service.ReportService;
import com.ytyo.Service.RoomAdminService;
import com.ytyo.Service.RoomInfoService;
import com.ytyo.Utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@Slf4j
public class ManageRoomController {

    @Autowired
    RoomAdminService roomAdminService;

    @Autowired
    OperatingService operatingService;
    @Autowired
    LiveAccountApi liveAccountApi;

    @Autowired
    LiveStreamingApi liveStreamingApi;
    @Autowired
    RoomInfoService roomInfoService;

    @Autowired
    LiveStoreApi liveStoreApi;
    @Autowired
    ReportService reportService;
    @Autowired
    ObjectMapper objectMapper;

    //生成禁言记录
    //远程调用该接口生成记录成功后即鉴权成功

    /**
     * @param type 0: 禁言 ,其他非空值: 解禁言
     */
    @PostMapping("/generateBannedPostRecord")
    @Remote
    public ResponseEntity<?> generateBannedPostRecord(Long roomId, Long userId, Integer type, HttpServletRequest request) {
        if (roomId == null || userId == null || type == null) {
            return ResponseEntity.badRequest().body("错误的请求!");
        }

        String operationName = type == 0 ? "禁言" : "解禁言";
        try {
            User admin = RequestUtil.getUserByReq(request).unwrap();
            if (Objects.equals(admin.getRole(), 2)) {
                log.info("超级管理员{}操作", operationName);
            }


            //由于是禁言，与这里RoomInfo是否存在关系不大,所以无需判断房间是否存在
            boolean isRoomAdmin = roomAdminService.isRoomAdmin(admin.getId(), roomId);
            //操作者是否为房间主播
            boolean isRoomAnchor = roomInfoService.isRoomAnchor(roomId, admin.getId());

            if (!isRoomAdmin && admin.getRole() < 2 && !isRoomAnchor) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权调用");
            }


            if (roomInfoService.isRoomAnchor(roomId, userId)) {
                return ResponseEntity.badRequest().body("不能对房间主播" + operationName + "!");
            }

            User user = liveAccountApi.getUserById(userId).unwrap();

            if (Objects.equals(user.getId(), admin.getId())) {
                return ResponseEntity.badRequest().body("自己不能" + operationName + "自己");
            }

            if (user.getRole() > admin.getRole()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("不能操作比自己权限更高的用户!");
            }
            //房管或房间主播才能同权禁用
            if (!isRoomAdmin && !isRoomAnchor &&Objects.equals(user.getRole(), admin.getRole())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("不能操作比自己权限更高的用户!");
            }


            OperationUser operation = new OperationUser();
            operation.setRoomId(roomId);
            operation.setTargetUserId(userId);
            operation.setOperating(operationName);
            operation.setOptionDescription(String.format("房间%s中,管理员%s将用户%s%s", roomId, admin.getNickname(), user.getNickname(), operationName));

            OperatingRecord operatingRecord = new OperatingRecord();
            operatingRecord.setUserId(admin.getId());
            operatingRecord.setOperation(objectMapper.writeValueAsString(operation));


            if (!operatingService.generateOperatingRecord(operatingRecord)) {
                return ResponseEntity.badRequest().body("生成记录失败!");
            }
            return ResponseEntity.ok("生成成功!");
        } catch (NoneException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("生成失败!");
        }
    }

    @PostMapping("/bannedLive")
    public ResponseEntity<?> bannedLive(Long roomId, HttpServletRequest request) {
        if (roomId == null) {
            return ResponseEntity.badRequest().body("错误的请求!");
        }
        try {
            User admin = RequestUtil.getUserByReq(request).unwrap();
            if (admin.getRole() < 2) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权调用");
            }

            RoomDetails.RoomInfo roomInfo = roomInfoService.getRoomInfoById(roomId).unwrap();

            User anchor = liveAccountApi.getUserById(roomInfo.getAnchorId()).unwrap();
            if (anchor.getRole() >= admin.getRole()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("不能操作比自己权限高的用户!");
            }

            if (!roomInfoService.roomCanLive(roomId)) {
                return ResponseEntity.badRequest().body("主播已被封禁");
            }

            //实现禁止进入
            if (!roomInfoService.changeRoomInfoCanLive(roomId, (short) 0)) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("操作失败!!");
            }


            //通知远端下播
            if (!liveStreamingApi.offlineRoom(roomId)) {
                log.error("通知下播失败!");
            }

            OperatingRoom operation = new OperatingRoom();
            operation.setTargetRoomId(roomId);
            operation.setOperating("封禁");
            operation.setOptionDescription("超级管理员:" + admin.getNickname() + "  将房间号" + roomId + "禁播");

            OperatingRecord operatingRecord = new OperatingRecord();
            operatingRecord.setUserId(admin.getId());
            operatingRecord.setOperation(objectMapper.writeValueAsString(operation));

            if (!operatingService.generateOperatingRecord(operatingRecord)) {
                log.error("生成禁播记录失败!");
            }
            return ResponseEntity.ok("操作成功!");
        } catch (NoneException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("操作失败!");
        }
    }

    @GetMapping("/getRoomAdminIds")
    @RemoteNoLoginRequired
    @Remote
    private ResponseEntity<?> getRoomAdminIds(Long anchorId) {
        if (anchorId == null) {
            return ResponseEntity.badRequest().body("参数错误!");
        }
        try {
            RoomDetails.RoomInfo roomInfo = roomInfoService.getRoomInfoByAnchorId(anchorId).unwrap();
            return ResponseEntity.ok(roomAdminService.selectRoomAdminsByRoomId(roomInfo.getId()).unwrap());
        } catch (NoneException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("未找到主播的房间");
        }
    }

    @PostMapping("/removeOperating")
    public ResponseEntity<?> removeOperating(Long recordId, HttpServletRequest request) throws NoneException {
        if (recordId == null) {
            return ResponseEntity.badRequest().body("参数错误!");
        }
        User user = RequestUtil.getUserByReq(request).unwrap();
        if (!operatingService.hiddenOperating(recordId, user.getId())) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("移除操作记录失败!");
        }
        return ResponseEntity.ok("移除成功!");
    }


    @PostMapping("/addRoomAdmin")
    @RemoteNoLoginRequired
    @Remote
    public ResponseEntity<?> addRoomAdmin(Long anchorId, Long userId) {
        if (anchorId == null || userId == null) {
            return ResponseEntity.badRequest().body("错误的请求");
        }
        try {
            RoomDetails.RoomInfo roomInfo = roomInfoService.getRoomInfoByAnchorId(anchorId).unwrap();
            boolean b = roomAdminService.addRoomAdmin(userId, roomInfo.getId());
            if (!b) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("添加失败");
            }
            return ResponseEntity.ok("添加成功!");
        } catch (NoneException e) {
            return ResponseEntity.badRequest().body("添加失败!");
        }
    }

    @PostMapping("/deleteRoomAdmin")
    @RemoteNoLoginRequired
    @Remote
    public ResponseEntity<?> deleteRoomAdmin(Long anchorId, Long userId) {
        if (anchorId == null || userId == null) {
            return ResponseEntity.badRequest().body("错误的请求");
        }
        try {
            RoomDetails.RoomInfo roomInfo = roomInfoService.getRoomInfoByAnchorId(anchorId).unwrap();
            boolean b = roomAdminService.deleteRoomAdmin(userId, roomInfo.getId());
            if (!b) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("添加失败");
            }
            return ResponseEntity.ok("添加成功!");
        } catch (NoneException e) {
            return ResponseEntity.badRequest().body("添加失败!");
        }
    }


    @GetMapping("/getOperations")
    public ResponseEntity<?> getOptionals(Short type, HttpServletRequest request) {
        ArrayList<OperatingRecordModel> result = new ArrayList<>();
        List<OperatingRecord> records;

        try {
            User user = RequestUtil.getUserByReq(request).unwrap();
            records = operatingService.getDisplayOperatingRecordsByUserId(user.getId()).unwrap();
        } catch (NoneException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("查询失败!");
        }


        switch (type) {
            //对房间操作记录
            case 1 -> {
                for (OperatingRecord record : records) {
                    if (record.getDisplay() != 1) {
                        continue;
                    }
                    try {
                        OperatingRecordModel model = new OperatingRecordModel();
                        model.setRecordId(record.getId());
                        model.setOperatingTime(record.getCreateTime());
                        OperatingRoom operatingRoom = objectMapper.readValue(record.getOperation(), OperatingRoom.class);
                        model.setType((short) 1);
                        Long roomId = operatingRoom.getTargetRoomId();
                        RoomDetails.RoomInfo roomInfo = roomInfoService.getRoomInfoById(roomId).unwrap();
                        Long anchorId = roomInfo.getAnchorId();
                        User anchor = liveAccountApi.getUserById(anchorId).unwrap();
                        model.setRoomId(roomId);
                        model.setAnchorNickname(anchor.getNickname());
                        model.setOperating(StringUtils.hasText(operatingRoom.getOperating()) ? operatingRoom.getOperating() : "封禁");
                        result.add(model);
                    } catch (JsonProcessingException | NoneException ignored) {
                    }
                }
            }
            //对用户操作记录
            case 0 -> {
                for (OperatingRecord record : records) {
                    if (record.getDisplay() != 1) {
                        continue;
                    }
                    try {
                        OperatingRecordModel model = new OperatingRecordModel();
                        model.setRecordId(record.getId());
                        model.setOperatingTime(record.getCreateTime());
                        OperationUser operationUser = objectMapper.readValue(record.getOperation(), OperationUser.class);
                        model.setType((short) 0);
                        User targetUser = liveAccountApi.getUserById(operationUser.getTargetUserId()).unwrap();
                        RoomDetails.RoomInfo roomInfo = roomInfoService.getRoomInfoById(operationUser.getRoomId()).unwrap();
                        User anchor = liveAccountApi.getUserById(roomInfo.getAnchorId()).unwrap();
                        model.setRoomId(roomInfo.getId());
                        model.setUserNickname(targetUser.getNickname());
                        model.setAnchorNickname(anchor.getNickname());
                        model.setOperating(StringUtils.hasText(operationUser.getOperating()) ? operationUser.getOperating() : "禁言");
                        result.add(model);
                    } catch (JsonProcessingException | NoneException ignored) {
                    }
                }
            }
//            全部操作记录
            case -1 -> {
                try {
                    //todo 优化
                    for (OperatingRecord record : records) {
                        if (record.getDisplay() != 1) {
                            continue;
                        }
                        try {
                            OperatingRecordModel model = new OperatingRecordModel();
                            model.setRecordId(record.getId());
                            model.setOperatingTime(record.getCreateTime());
                            OperatingRoom operatingRoom = objectMapper.readValue(record.getOperation(), OperatingRoom.class);
                            model.setType((short) 1);
                            Long roomId = operatingRoom.getTargetRoomId();
                            RoomDetails.RoomInfo roomInfo = roomInfoService.getRoomInfoById(roomId).unwrap();
                            Long anchorId = roomInfo.getAnchorId();
                            User anchor = liveAccountApi.getUserById(anchorId).unwrap();
                            model.setRoomId(roomId);
                            model.setAnchorNickname(anchor.getNickname());
                            if (StringUtils.hasText(operatingRoom.getOperating())) {
                                model.setOperating(operatingRoom.getOperating());
                            } else {
                                model.setOperating("封禁");
                            }
                            result.add(model);
                        } catch (JsonProcessingException e) {
                            try {
                                OperatingRecordModel model = new OperatingRecordModel();
                                model.setRecordId(record.getId());
                                model.setOperatingTime(record.getCreateTime());
                                OperationUser operationUser = objectMapper.readValue(record.getOperation(), OperationUser.class);
                                model.setType((short) 0);
                                User targetUser = liveAccountApi.getUserById(operationUser.getTargetUserId()).unwrap();
                                RoomDetails.RoomInfo roomInfo = roomInfoService.getRoomInfoById(operationUser.getRoomId()).unwrap();
                                User anchor = liveAccountApi.getUserById(roomInfo.getAnchorId()).unwrap();
                                model.setRoomId(roomInfo.getId());
                                model.setUserNickname(targetUser.getNickname());
                                model.setAnchorNickname(anchor.getNickname());
                                model.setOperating(StringUtils.hasText(operationUser.getOperating()) ? operationUser.getOperating() : "禁言");
                                result.add(model);
                            } catch (JsonProcessingException | NoneException ex) {
                                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("查询失败!");
                            }
                        }
                    }
                } catch (NoneException e) {
                    return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("查询失败!");
                }
            }
        }
        return ResponseEntity.ok(result);
    }

    //todo
//    /**
//     * 管理员查询所有自己禁言过的用户，并返回所有的OperatingRecordModel，userId不能重复
//     *
//     * @param request
//     * @return
//     */
//    @GetMapping("/getBanList")
//    public ResponseEntity<?> getBanList(HttpServletRequest request) {
//
//
//    }

    @GetMapping("/isRoomAdmin")
    public ResponseEntity<?> isRoomAdmin(Long roomId, HttpServletRequest request) {
        if (roomId == null) {
            return ResponseEntity.badRequest().body("错误的请求");
        }
        try {
            User user = RequestUtil.getUserByReq(request).unwrap();
            return ResponseEntity.ok(roomAdminService.isRoomAdmin(user.getId(), roomId));
        } catch (NoneException e) {
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/takeOfficeList")
    public ResponseEntity<?> takeOfficeList(HttpServletRequest request) {
        try {
            User user = RequestUtil.getUserByReq(request).unwrap();
            ArrayList<JobOffice> jobOffices = new ArrayList<>();
            if (user.getRole() == 2) {
                //todo 修正任职时间
                jobOffices.add(new JobOffice("超管", "---", "所有房间"));
            }
            List<RoomAdmin> admins = roomAdminService.selectRoomAdminsByUserId(user.getId()).unwrap();
            for (RoomAdmin admin : admins) {
                JobOffice jobOffice = new JobOffice("房管", admin.getCreateTime().toString(), "房间号:" + admin.getRoomId());
                jobOffices.add(jobOffice);
            }
            return ResponseEntity.ok(jobOffices);
        } catch (NoneException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("查询失败!");
        }
    }

    @PostMapping("/report")
    public ResponseEntity<?> report(@RequestBody @Validated ReportExtension reportExtension, HttpServletRequest
            request) {
        if (reportExtension == null)
            return ResponseEntity.badRequest().body("错误的请求");
        Option<RoomDetails.RoomInfo> roomInfo = roomInfoService.getRoomInfoById(reportExtension.getRoomId());
        if (roomInfo.isNone()) {
            return ResponseEntity.badRequest().body("房间不存在");
        }
        try {
            User user = RequestUtil.getUserByReq(request).unwrap();
            if (reportService.getRoomReportByUserId(roomInfo.data().getId(), user.getId()).isSome()) {
                return ResponseEntity.badRequest().body("您已经举报过了");
            }
            Option<List<String>> images = liveStoreApi.saveImages(reportExtension.getScreenshotBase64s(), "report", request);
            if (images.isNone()) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("举报截图存储失败");
            }
            reportExtension.setScreenshotPaths(images.unwrap().toArray(new String[0]));
            Report report = new Report(reportExtension);
            report.setUserId(user.getId());
            report.setUserNickname(user.getNickname());
            report.setAnchorId(roomInfo.unwrap().getAnchorId());
            if (!reportService.report(report)) {
                return ResponseEntity.badRequest().body("举报失败!");
            }
            return ResponseEntity.ok("举报成功，请等待管理员审核");
        } catch (NoneException e) {
            return ResponseEntity.badRequest().body("未登录");
        }
    }

    @GetMapping("/getReports")
    @SuperAdmin
    public ResponseEntity<?> getReports() {
        Map<Long, List<Report>> roomReports = reportService.getOverThresholdRoomReports(0);
        ArrayList<ReportInfo> reportInfos = new ArrayList<>();

        //todo 优化
        roomReports.forEach((key, value) -> {
            try {
                Option<User> anchor = liveAccountApi.getUserById(value.get(0).getAnchorId());
                if (anchor.isSome()) {
                    ReportInfo reportInfo = new ReportInfo(key, anchor.unwrap().getAvatar(), anchor.unwrap().getNickname(), value);
                    reportInfos.add(reportInfo);
                }
            } catch (NoneException e) {
                throw new RuntimeException(e);
            }
        });

        return ResponseEntity.ok(reportInfos);
    }

    //删除该房间的举报记录，即超级管理员已处理
    @PostMapping("/removeReport")
    @SuperAdmin
    public ResponseEntity<?> removeReport(Long roomId, HttpServletRequest request) {
        if (roomId == null) {
            return ResponseEntity.badRequest().body("错误的请求");
        }
        //生成操作记录
        try {
            User user = RequestUtil.getUserByReq(request).unwrap();
            OperatingRecord operatingRecord = new OperatingRecord();
            operatingRecord.setUserId(user.getId());
            OperatingRoom operatingRoom = new OperatingRoom();
            operatingRoom.setTargetRoomId(roomId);
            operatingRoom.setOperating("举报记录删除");
            operatingRoom.setOptionDescription(String.format("超级管理员%s,将房间%s的举报记录删除", user.getNickname(), roomId));
            operatingRecord.setOperation(objectMapper.writeValueAsString(operatingRoom));
            operatingService.generateOperatingRecord(operatingRecord);
        } catch (NoneException | JsonProcessingException e) {
            return ResponseEntity.badRequest().body("操作失败!");
        }

        //删除举报记录
        reportService.deleteRoomReport(roomId);

        return ResponseEntity.ok("删除成功");
    }

}
