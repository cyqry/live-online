package com.ytyo.Model.ElasticSearch.Category;

import lombok.Data;


//其他类不存Category的id，以便Category随时增删
@Data
public abstract class AbstractCategory {
    protected String name;
}
