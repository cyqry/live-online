package com.ytyo.annotation.vaild.Validator;

import com.ytyo.annotation.vaild.EnumString;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class EnumStringValidator implements ConstraintValidator<EnumString, String> {
    private List<String> enumStringList;

    public EnumStringValidator() {
        System.out.println("校验类！");
    }

    @Override
    public void initialize(EnumString constraintAnnotation) {
        enumStringList = Arrays.asList(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            System.out.println("string value 为空");
            return true;
        }

        return enumStringList.contains(value);
    }
}