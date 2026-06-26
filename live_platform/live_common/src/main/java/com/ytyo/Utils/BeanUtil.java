package com.ytyo.Utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class BeanUtil {
    /**
     * 将source中非空字段放到target中去
     */
    public static <T> void updateNonNullFields(T source, T target) throws IllegalAccessException {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Source and target 都不能为null");
        }

        if (!source.getClass().equals(target.getClass())) {
            throw new IllegalArgumentException("Source and target 需要是同一个类");
        }

        for (Field field : source.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(source);
            if (value != null) {
                field.set(target, value);
            }
        }
    }

    public static <T> Map<String, Object> extractNonNullFields(T obj) {

        Map<String, Object> fieldsMap = new HashMap<>();
        if (obj == null)
            return fieldsMap;
        Stream.of(obj.getClass().getDeclaredFields())
                .peek(field -> field.setAccessible(true))
                .forEach(field -> {
                    try {
                        Object value = field.get(obj);
                        if (value != null) {
                            fieldsMap.put(field.getName(), value);
                        }
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException("代码错误!");
                    }
                });
        return fieldsMap;
    }
}

