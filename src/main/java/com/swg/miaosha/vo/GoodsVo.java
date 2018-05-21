package com.swg.miaosha.vo;

import lombok.Data;

import java.util.Date;

@Data
public class GoodsVo{
    private Long id;
    private String goodsName;
    private String goodsTitle;
    private String goodsImg;
    private String goodsDetail;
    private Double goodsPrice;
    private Integer goodsStock;

    private double miaoshaPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
