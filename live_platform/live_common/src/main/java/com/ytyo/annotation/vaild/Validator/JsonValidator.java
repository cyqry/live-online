package com.ytyo.annotation.vaild.Validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ytyo.annotation.vaild.Json;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class JsonValidator implements ConstraintValidator<Json, String> {
    private ObjectMapper mapper;
    private boolean required = false;
    private Class<?>[] classes;

    @Override
    public void initialize(Json constraintAnnotation) {
        mapper = new ObjectMapper();
        required = constraintAnnotation.value();
        classes = constraintAnnotation.classes();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null && !required)
            return true;
        if (value == null)
            return false;
        if (classes == null) {
            return true;
        }
        try {
            for (Class<?> aClass : classes) {
                Object o = mapper.readValue(value, aClass);
                if (o == null)
                    return false;
            }
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }
}
