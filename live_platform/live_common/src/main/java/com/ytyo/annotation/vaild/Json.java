package com.ytyo.annotation.vaild;

import com.ytyo.annotation.vaild.Validator.JsonValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Json.List.class)
@Documented
@Constraint(validatedBy = JsonValidator.class)//标明由哪个类执行校验逻辑
public @interface Json {
    String message() default "property格式错误";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    /**
     * @return 是否必须为非空
     */
    boolean value() default false;

    Class<?>[] classes();

    /**
     * Defines several {@link Json} annotations on the same element.
     *
     * @see Json
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        Json[] value();
    }
}
