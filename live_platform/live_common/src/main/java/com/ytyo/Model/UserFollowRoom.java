package com.ytyo.Model;

import lombok.Data;

import java.time.LocalDateTime;

//不实现序列化接口会 406
@Data
public class UserFollowRoom {
    Long userId;
    Long roomId;
    LocalDateTime followTime;
}
