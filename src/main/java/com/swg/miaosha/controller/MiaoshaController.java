package com.swg.miaosha.controller;

import com.swg.miaosha.key.GoodsKey;
import com.swg.miaosha.model.MiaoshaOrder;
import com.swg.miaosha.model.MiaoshaUser;
import com.swg.miaosha.model.OrderInfo;
import com.swg.miaosha.mq.MQSender;
import com.swg.miaosha.mq.MiaoshaMessage;
import com.swg.miaosha.redis.RedisService;
import com.swg.miaosha.result.CodeMsg;
import com.swg.miaosha.result.Result;
import com.swg.miaosha.service.GoodsService;
import com.swg.miaosha.service.MiaoshaService;
import com.swg.miaosha.service.OrderService;
import com.swg.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean{

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MiaoshaService miaoshaService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MQSender sender;

    private Map<Long,Boolean> localOverMap = new HashMap<>();

    /**
     * 系统初始化查询秒杀商品库存
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.getGoodsVoList();
        if(goodsVoList == null){
            return;
        }
        for(GoodsVo goods:goodsVoList){
            redisService.set(GoodsKey.getMiaoshaGoodsStock,""+goods.getId(),goods.getStockCount());
            localOverMap.put(goods.getId(),false);
        }
    }

    @RequestMapping(value = "/do_miaosha",method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> do_miaosha(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId){
        if(user == null)
            return Result.error(CodeMsg.SESSION_ERROR);

        //内存标记，减少不必要的redis的访问
        boolean over = localOverMap.get(goodsId);
        if(over){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //预减库存进行优化
        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock,""+goodsId);
        if(stock < 0){
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if(miaoshaOrder != null){
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //进入消息队列
        MiaoshaMessage message = new MiaoshaMessage();
        message.setUser(user);
        message.setGoodsId(goodsId);
        sender.sendMiaoshaMessage(message);

        return Result.success(0);//排队中


        /*
        //判断库存
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        if(goodsVo.getStockCount() <= 0){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }


        //判断是否已经秒杀到了
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if(miaoshaOrder != null){
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }


        //减库存、下订单、写入秒杀订单,需要在一个事务中执行
        OrderInfo orderInfo = miaoshaService.miaosha(user,goodsVo);

        return Result.success(orderInfo);

        */
    }

    /**
     * 秒杀成功，返回orderId
     * -1：秒杀失败
     * 0：排队中
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> result(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId){
        if(user == null)
            return Result.error(CodeMsg.SESSION_ERROR);

        long result = miaoshaService.getMiaoshaResult(user.getId(),goodsId);
        return Result.success(result);
    }

}
