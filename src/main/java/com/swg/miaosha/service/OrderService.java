package com.swg.miaosha.service;

import com.swg.miaosha.constants.Constants;
import com.swg.miaosha.dao.OrderDao;
import com.swg.miaosha.key.OrderKey;
import com.swg.miaosha.model.MiaoshaOrder;
import com.swg.miaosha.model.MiaoshaUser;
import com.swg.miaosha.model.OrderInfo;
import com.swg.miaosha.redis.RedisService;
import com.swg.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private RedisService redisService;


    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(Long userId, long goodsId) {
        //return orderDao.getMiaoshaOrderByUserIdGoodsId(userId,goodsId);
        return redisService.get(OrderKey.getMiaoShaOrderByUidGid,""+userId+"_"+goodsId, MiaoshaOrder.class);
    }

    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(Constants.OrderStatus.NOT_PAID.getStatus());//新建未支付
        orderInfo.setUserId(user.getId());

        orderDao.insert(orderInfo);

        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goods.getId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(user.getId());

        orderDao.insertMiaoshaOrder(miaoshaOrder);

        redisService.set(OrderKey.getMiaoShaOrderByUidGid,""+user.getId()+"_"+goods.getId(), miaoshaOrder);

        return orderInfo;
    }

    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }
}
