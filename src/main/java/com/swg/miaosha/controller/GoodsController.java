package com.swg.miaosha.controller;

import com.swg.miaosha.constants.Constants;
import com.swg.miaosha.dao.GoodsDao;
import com.swg.miaosha.model.MiaoshaUser;
import com.swg.miaosha.service.GoodsService;
import com.swg.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    @RequestMapping("to_list")
    public String toList(Model model,MiaoshaUser user){
        if(user == null)
            return "login";
        model.addAttribute("user",user);
        List<GoodsVo> goodsVoList = goodsService.getGoodsVoList();
        model.addAttribute("goodsList",goodsVoList);
        return "goods_list";
    }

    @RequestMapping("/to_detail/{goodsId}")
    public String toList(@PathVariable("goodsId") long goodsId,Model model, MiaoshaUser user){
        if(user == null)
            return "login";
        model.addAttribute("user",user);

        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods",goodsVo);
        long startAt = goodsVo.getStartDate().getTime();
        long endAt = goodsVo.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int miaoshaStatus = 0;//秒杀活动的状态，0-秒杀前；1-正在秒杀；2-秒杀结束
        int remainSeconds = 0;//秒杀活动还剩多少秒
        if(now < startAt){
            miaoshaStatus = Constants.MiaoshaStatus.BEFORE_START;
            remainSeconds = (int)(startAt-now)/1000;
        }else if (now > endAt){
            miaoshaStatus = Constants.MiaoshaStatus.AFTER_MIAOSHA;
            remainSeconds = -1;
        }else {
            miaoshaStatus = Constants.MiaoshaStatus.ON_MIAOSHA;
            remainSeconds = 0;
        }

        model.addAttribute("miaoshaStatus",miaoshaStatus);
        model.addAttribute("remainSeconds",remainSeconds);
        return "goods_detail";
    }
}
