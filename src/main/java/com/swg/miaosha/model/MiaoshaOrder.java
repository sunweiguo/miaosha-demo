package com.swg.miaosha.model;

import lombok.Data;

@Data
public class MiaoshaOrder {
	private Long id;
	private Long userId;
	private Long  orderId;
	private Long goodsId;
}
