package com.ytyo.annotation.vaild.Validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ytyo.annotation.vaild.Property;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PropertyValidator implements ConstraintValidator<Property, String> {
    private ObjectMapper mapper;
    private boolean required = false;


    @Override
    public void initialize(Property constraintAnnotation) {
        mapper = new ObjectMapper();
        required = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null && !required)
            return true;
        if (value == null)
            return false;
        try {
            com.ytyo.Model.Property property = mapper.readValue(value, com.ytyo.Model.Property.class);
            return property != null && property.getCurrencies() != null && property.getCurrencies().stream().noneMatch(currency -> currency == null || currency.getCurrencyId() == null || currency.getCount() == null);
        } catch (JsonProcessingException e) {
            return false;
        }
    }
}
