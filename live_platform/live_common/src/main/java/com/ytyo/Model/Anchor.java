package com.ytyo.Model;

import com.ytyo.annotation.vaild.Json;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class Anchor {
    /**
     * 主播id
     */
    private Long id;

    /**
     * 主播介绍
     */
    @Length(min = 1, max = 50)
    private String description;

    /**
     * 主播收到的礼物
     */
    @Json(classes = AnchorProperty.class)
    private String anchorProperty;
}
