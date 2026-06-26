package com.ytyo.annotation.vaild;

import com.ytyo.annotation.vaild.Validator.EnumShortValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(EnumShort.List.class)
@Documented
@Constraint(validatedBy = EnumShortValidator.class)//标明由哪个类执行校验逻辑
public @interface EnumShort {
    String message() default "value not in enum values.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * @return date must in this value array
     */
    short[] value();

    /**
     * Defines several {@link EnumShort} annotations on the same element.
     *
     * @see EnumShort
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {

        EnumShort[] value();
    }
}