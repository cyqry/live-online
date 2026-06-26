package com.ytyo.Model;

import com.ytyo.Model.ElasticSearch.FirstLevel;
import com.ytyo.Model.ElasticSearch.SecondLevel;

import java.util.List;

public record ClassifyCategory(FirstLevel firstLevel, List<SecondLevel> secondLevels) {
}
