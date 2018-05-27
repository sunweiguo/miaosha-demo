package com.swg.miaosha.mq;

import com.swg.miaosha.model.MiaoshaOrder;
import com.swg.miaosha.model.MiaoshaUser;
import com.swg.miaosha.model.OrderInfo;
import com.swg.miaosha.redis.RedisService;
import com.swg.miaosha.result.CodeMsg;
import com.swg.miaosha.result.Result;
import com.swg.miaosha.service.GoodsService;
import com.swg.miaosha.service.MiaoshaService;
import com.swg.miaosha.service.OrderService;
import com.swg.miaosha.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author 【swg】.
 * @Date 2018/5/26 14:48
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@Service
@Slf4j
public class MQReceiver {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MiaoshaService miaoshaService;

    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receive(String message){
        log.info("receive message:{}",message);
        MiaoshaMessage msg = RedisService.stringToBean(message,MiaoshaMessage.class);
        MiaoshaUser user = msg.getUser();
        long goodsId = msg.getGoodsId();
        //判断数据库库存是否真的足够
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        if(goodsVo.getStockCount() <= 0){
            return;
        }
        //判断是否已经秒杀到了
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if(miaoshaOrder != null){
            return;
        }


        //减库存、下订单、写入秒杀订单,需要在一个事务中执行
        OrderInfo orderInfo = miaoshaService.miaosha(user,goodsVo);

    }
}
