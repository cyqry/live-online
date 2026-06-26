package com.ytyo.Model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnchorRegistrationInfo {
    @NotBlank
    String realName;

    @NotBlank
    String idNumber;

    @NotBlank
    String password;


    String description;

    @NotBlank
    String roomItemCategoryId;
}
