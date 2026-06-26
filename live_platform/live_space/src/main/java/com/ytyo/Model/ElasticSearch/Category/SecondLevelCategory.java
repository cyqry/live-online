package com.ytyo.Model.ElasticSearch.Category;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString
public class SecondLevelCategory extends AbstractCategory {
    public SecondLevelCategory(String name, String imagePath, String firstLevelId) {
        this.name = name;
        this.imagePath = imagePath;
        this.firstLevelId = firstLevelId;
    }

    String imagePath;
    String firstLevelId;
}
