package com.james.secondkill.access;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 07 14:48
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {
    /**
     * 最大请求访问次数
     */
    int seconds();

    /**
     * 最大请求数
     */
    int maxAccessCount();

    /**
     * 是否需要再次登录
     */
    boolean needLogin() default true;
}
