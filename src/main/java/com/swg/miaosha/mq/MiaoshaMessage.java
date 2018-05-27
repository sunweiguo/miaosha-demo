package com.swg.miaosha.mq;

import com.swg.miaosha.model.MiaoshaUser;
import lombok.Data;

/**
 * @Author 【swg】.
 * @Date 2018/5/26 21:03
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@Data
public class MiaoshaMessage {
    private MiaoshaUser user;
    private long goodsId;
}
