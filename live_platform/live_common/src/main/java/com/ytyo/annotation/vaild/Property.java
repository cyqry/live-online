package com.ytyo.annotation.vaild;

import com.ytyo.annotation.vaild.Validator.PropertyValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Property.List.class)
@Documented
@Constraint(validatedBy = PropertyValidator.class)//标明由哪个类执行校验逻辑
public @interface Property {
    String message() default "property格式错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * @return 是否必须为非空
     */
    boolean value() default false;

    /**
     * Defines several {@link Property} annotations on the same element.
     *
     * @see Property
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        Property[] value();
    }
}
