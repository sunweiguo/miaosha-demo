package com.swg.miaosha.constants;

public class Constants {
    //秒杀的状态
    public interface MiaoshaStatus{
        Integer BEFORE_START = 0;//秒杀前
        Integer ON_MIAOSHA = 1;//正在秒杀
        Integer AFTER_MIAOSHA = 2;//秒杀结束
    }

    //订单状态
    public enum  OrderStatus{
        NOT_PAID(0,"未支付"),
        PAID(1,"已支付"),
        SENT(2,"已发货"),
        RECEIVED(3,"已收货"),
        FINISHED(4,"已完成"),;

        private Integer status;
        private String message;
        OrderStatus(Integer status,String message){
            this.status = status;
            this.message = message;
        }

        public Integer getStatus(){
            return status;
        }
    }
}
