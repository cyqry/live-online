package com.ytyo.Model.Operating;

import com.ytyo.annotation.vaild.EnumShort;
import com.ytyo.annotation.vaild.Json;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperatingRecord {
    Long id;
    Long userId;

    @Json(classes = {OperationUser.class, OperatingRoom.class})
    String operation;

    @EnumShort(value = {(short) 1, (short) 0})
    Short display;
    LocalDateTime createTime;
    LocalDateTime updateTime;
}
