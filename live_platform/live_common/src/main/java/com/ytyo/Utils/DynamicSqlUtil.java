package com.ytyo.Utils;

import com.ytyo.Model.User;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class DynamicSqlUtil {

    public static String generateUpdateSql(Map<?, ?> paramMap) throws IllegalAccessException {

        Object model = paramMap.get("model");
        String tableName = camelToSnake(model.getClass().getSimpleName());
        Class<?> clazz;
        if ("userextension".equals(tableName)) {
            clazz = User.class;
            tableName = "user";
        } else
            clazz = model.getClass();

        StringBuilder updateSql = new StringBuilder("UPDATE " + tableName + " SET ");
        Map<String, Object> fieldMap = new HashMap<>();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(model);


            if (value != null) {
                if (value instanceof String s)
                    if (!StringUtils.hasText(s))
                        break;
                if (!"id".equals(field.getName()))
                    fieldMap.put(field.getName(), value);
            }
        }

        if (fieldMap.isEmpty()) {
            throw new IllegalArgumentException("没有要更新的字段");
        }

        fieldMap.forEach((key, value) -> {
            updateSql.append(camelToSnake(key)).append(" = #{model.").append(key).append("}, ");
        });

        updateSql.delete(updateSql.length() - 2, updateSql.length()); // 删除多余的逗号和空格
        updateSql.append(" WHERE id = #{model.id}");
        return updateSql.toString();
    }

    @Test
    public void test() {
        System.out.println(camelToSnake("User"));
    }

    public static String camelToSnake(String camelStr) {
        StringBuilder snakeStr = new StringBuilder();
        for (char c : camelStr.toCharArray()) {
            if (Character.isUpperCase(c)) {
                if (snakeStr.length() > 0) {
                    snakeStr.append('_');
                }
                snakeStr.append(Character.toLowerCase(c));
            } else {
                snakeStr.append(c);
            }
        }
        return snakeStr.toString();
    }
}
