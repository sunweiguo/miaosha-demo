package com.swg.miaosha.service;

import com.swg.miaosha.dao.MiaoshaUserDao;
import com.swg.miaosha.exception.GlobalException;
import com.swg.miaosha.key.MiaoshaKey;
import com.swg.miaosha.key.MiaoshaUserKey;
import com.swg.miaosha.model.MiaoshaOrder;
import com.swg.miaosha.model.MiaoshaUser;
import com.swg.miaosha.model.OrderInfo;
import com.swg.miaosha.redis.RedisService;
import com.swg.miaosha.result.CodeMsg;
import com.swg.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MiaoshaService {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MiaoshaUserDao miaoshaUserDao;

    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        //减库存、下订单、写入秒杀订单
        boolean success =goodsService.reduceStock(goods);
        if(success){
            return orderService.createOrder(user,goods);
        }else{
            setGoodsOver(goods.getId());
            return null;
        }
    }

    public MiaoshaUser getById(long id){
        //先去缓存取
        MiaoshaUser user = redisService.get(MiaoshaUserKey.getById,""+id,MiaoshaUser.class);
        if(user != null){
            return user;
        }
        //缓存没有则去数据库取
        user = miaoshaUserDao.getById(id);
        if(user != null){
            redisService.set(MiaoshaUserKey.getById,""+user.getId(),user);
        }
        return user;
    }

    public boolean updateUsername(String token,long id,String newUsername){
        MiaoshaUser user = getById(id);
        if(user == null)
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        //更新数据库
        MiaoshaUser toBeUpdate = new MiaoshaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setNickname(newUsername);
        miaoshaUserDao.update(toBeUpdate);
        //处理缓存
        redisService.del(MiaoshaUserKey.getById,""+id);
        user.setNickname(newUsername);
        redisService.set(MiaoshaUserKey.token,token,user);//token不能直接删除，否则会要求重新登录
        return true;
    }

    public long getMiaoshaResult(Long userId, long goodsId) {
        MiaoshaOrder orderInfo = orderService.getMiaoshaOrderByUserIdGoodsId(userId,goodsId);
        if(orderInfo != null){
            return orderInfo.getId();
        }else{
            boolean isOver = getGoodsOver(goodsId);
            if(isOver){
                //库存已经没了
                return -1;
            }else{
                //表示还没入库，继续等待结果
                return 0;
            }
        }
    }

    private void setGoodsOver(long goodId){
        redisService.set(MiaoshaKey.isGoodsOver,""+goodId,true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(MiaoshaKey.isGoodsOver,""+goodsId);
    }
}
