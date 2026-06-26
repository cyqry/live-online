package com.ytyo.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobOffice {
    String roleName;
    String time;

    //管理对象
    String range;
}
