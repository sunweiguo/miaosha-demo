package com.swg.miaosha.access;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author 【swg】.
 * @Date 2018/5/27 14:23
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {

    int seconds();//缓存多长时间
    int maxCount();//规定时间内最大访问次数
    boolean needLogin() default true;//是否需要登陆

}
