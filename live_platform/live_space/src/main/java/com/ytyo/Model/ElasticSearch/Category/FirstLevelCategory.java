package com.ytyo.Model.ElasticSearch.Category;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * 不带id，存es的时候使用
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString
public class FirstLevelCategory extends AbstractCategory {
    public FirstLevelCategory(String name) {
        this.name = name;
    }
}
