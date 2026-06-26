package com.ytyo.Model.ElasticSearch;

import com.ytyo.Model.ElasticSearch.Category.FirstLevelCategory;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 即带有id的FirstLevelCategory,查es的时候作为返回对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FirstLevel extends FirstLevelCategory {
    String id;

    public FirstLevel(String id, FirstLevelCategory firstLevelCategory) {
        super(firstLevelCategory.getName());
        this.id = id;
    }

}
