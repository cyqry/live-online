package com.ytyo.Model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoomAdmin {
    Long userId;
    Long roomId;

    LocalDateTime createTime;
}
