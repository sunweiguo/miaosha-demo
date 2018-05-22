package com.swg.miaosha.vo;


import com.swg.miaosha.model.OrderInfo;
import lombok.Data;

@Data
public class OrderDetailVo {
	private GoodsVo goods;
	private OrderInfo order;
}
