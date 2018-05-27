package com.swg.miaosha.access;

import com.swg.miaosha.model.MiaoshaUser;

/**
 * @Author 【swg】.
 * @Date 2018/5/27 14:34
 * @DESC
 * @CONTACT 317758022@qq.com
 */
public class UserContext {
    private static ThreadLocal<MiaoshaUser> userHolder = new ThreadLocal<>();

    public static void setUser(MiaoshaUser user){
        userHolder.set(user);
    }

    public static MiaoshaUser getUser(){
        return userHolder.get();
    }
}
