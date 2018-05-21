package com.swg.miaosha.model;

import lombok.Data;

import java.util.Date;
@Data
public class MiaoshaGoods {
	private Long id;
	private Long goodsId;
	private double miaoshaPrice;
	private Integer stockCount;
	private Date startDate;
	private Date endDate;
}
