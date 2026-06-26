package com.ytyo.Model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperatingRecordModel {

    Long recordId;

    //操作类型, 0 为对用户 操作， 1 为对房间操作
    Short type;
    LocalDateTime operatingTime;

    //具体操作
    String operating;

    Long userId;
    String userNickname;
    Long roomId;
    String anchorNickname;
}
