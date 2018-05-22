package com.swg.miaosha.vo;

import com.swg.miaosha.model.MiaoshaUser;
import lombok.Data;

@Data
public class DetailVo {
    private int miaoshaStatus = 0;
    private int remainSeconds = 0;
    private GoodsVo goods;
    private MiaoshaUser user;
}
