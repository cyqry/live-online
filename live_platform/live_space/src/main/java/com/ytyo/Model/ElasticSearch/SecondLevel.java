package com.ytyo.Model.ElasticSearch;

import com.ytyo.Model.ElasticSearch.Category.SecondLevelCategory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class SecondLevel extends SecondLevelCategory {
    String id;

    @Override
    public String toString() {
        return "SecondLevel{" +
                "id='" + id + '\'' +
                ", imagePath='" + this.getImagePath() + '\'' +
                ", firstLevelId='" + this.getFirstLevelId() + '\'' +
                ", name='" + this.getName() + '\'' +
                '}';
    }

    public SecondLevel(String id, SecondLevelCategory secondLevelCategory) {
        super(secondLevelCategory.getName(), secondLevelCategory.getImagePath(), secondLevelCategory.getFirstLevelId());
        this.id = id;
    }


}
