package com.firefox.center.common.annotation;

import com.firefox.center.common.constrains.RegCheckValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 *  正则表达式校验注解
 * @Author sujie
 **/
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Constraint(validatedBy = RegCheckValidator.class)
@Retention(RUNTIME)
@Documented
public @interface RegCheck {

  String message() default "格式错误";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String value();

}
