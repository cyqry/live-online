package com.ytyo.annotation.vaild.Validator;

import com.ytyo.annotation.vaild.EnumShort;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;

public class EnumShortValidator implements ConstraintValidator<EnumShort, Short> {
    private List<Short> enumShortList;

    public EnumShortValidator() {
        System.out.println("校验类！");
    }

    @Override
    public void initialize(EnumShort constraintAnnotation) {
        enumShortList = new ArrayList<>();
        for (short i : constraintAnnotation.value()) {
            enumShortList.add(i);
        }
    }

    @Override
    public boolean isValid(Short value, ConstraintValidatorContext context) {
        if (value == null) {
            System.out.println("short value 为空");
            return true;
        }
        return enumShortList.contains(value);
    }
}