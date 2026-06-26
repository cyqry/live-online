package com.ytyo.Model.Operating;


import lombok.Data;

@Data
public class OperationUser {
    Long targetUserId;
    Long roomId;
    String operating;
    String optionDescription;
}

