package com.james.secondkill.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 04 20:03
 *
 * 手机号码的校验注解
 */

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {IsMobileValidator.class})
public @interface IsMobile {

    /**
     * 默认手机号码不为空
     * @return
     */
    boolean required() default true;

    /**
     * 如果不通过校验时的提示信息
     */
    String message() default "手机号码格式有误！！";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
