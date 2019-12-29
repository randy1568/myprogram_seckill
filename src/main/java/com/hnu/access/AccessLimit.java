package com.hnu.access;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {
    //表示redis里边，键的过期时间长度
    int seconds();
    //表示最大访问次数
    int maxCount();
    //表示是否要登陆
    boolean needLogin() default true;
}
