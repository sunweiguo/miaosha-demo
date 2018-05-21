package com.swg.miaosha.controller;

import com.swg.miaosha.model.MiaoshaOrder;
import com.swg.miaosha.model.MiaoshaUser;
import com.swg.miaosha.model.OrderInfo;
import com.swg.miaosha.result.CodeMsg;
import com.swg.miaosha.service.GoodsService;
import com.swg.miaosha.service.MiaoshaService;
import com.swg.miaosha.service.OrderService;
import com.swg.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MiaoshaService miaoshaService;

    @RequestMapping("/do_miaosha")
    public String do_miaosha(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId){
        if(user == null)
            return "login";
        model.addAttribute("user",user);
        //判断库存
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        if(goodsVo.getStockCount() <= 0){
            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }
        //判断是否已经秒杀到了
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if(miaoshaOrder != null){
            model.addAttribute("errmsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
            return "miaosha_fail";
        }
        //减库存、下订单、写入秒杀订单,需要在一个事务中执行
        OrderInfo orderInfo = miaoshaService.miaosha(user,goodsVo);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goodsVo);
        return "order_detail";
    }
}
