package com.ytyo.annotation.vaild.Validator;

import com.ytyo.annotation.vaild.EnumInt;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class EnumIntValidator implements ConstraintValidator<EnumInt, Integer> {
    private List<Integer> enumIntList;

    public EnumIntValidator() {
        System.out.println("校验类！");
    }

    @Override
    public void initialize(EnumInt constraintAnnotation) {
        enumIntList = Arrays.stream(constraintAnnotation.value())
                .boxed()
                .toList();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return enumIntList.contains(value);
    }
}