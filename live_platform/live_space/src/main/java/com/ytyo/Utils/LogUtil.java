package com.ytyo.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ytyo.Model.ElasticSearch.Category.SecondLevelCategory;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class LogUtil {

    @Test
    public void test() {

    }


    public static String mapLog(Map<?, ?> map) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return "Map:" + mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
