package com.swg.miaosha.key;

/**
 * @Author 【swg】.
 * @Date 2018/5/27 14:06
 * @DESC
 * @CONTACT 317758022@qq.com
 */
public class AccessKey extends BasePrefix{
    private AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static AccessKey withExpire(int expireSeconds){
        return new AccessKey(expireSeconds,"access");
    }
}
