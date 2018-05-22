package com.swg.miaosha.controller;

import com.swg.miaosha.constants.Constants;
import com.swg.miaosha.dao.GoodsDao;
import com.swg.miaosha.key.GoodsKey;
import com.swg.miaosha.model.MiaoshaUser;
import com.swg.miaosha.redis.RedisService;
import com.swg.miaosha.result.Result;
import com.swg.miaosha.service.GoodsService;
import com.swg.miaosha.vo.DetailVo;
import com.swg.miaosha.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;
    @Autowired
    private ApplicationContext applicationContext;

/*    @RequestMapping("to_list")
    public String toList(Model model,MiaoshaUser user){
        if(user == null)
            return "login";
        model.addAttribute("user",user);
        List<GoodsVo> goodsVoList = goodsService.getGoodsVoList();
        model.addAttribute("goodsList",goodsVoList);
        return "goods_list";
    }*/

    @RequestMapping(value = "to_list",produces = "text/html")
    @ResponseBody
    public String toList(Model model, MiaoshaUser user, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(user == null){
            response.sendRedirect("/login/to_login");
            return null;
        }

        model.addAttribute("user",user);
        //先尝试从缓存中取
        String html = redisService.get(GoodsKey.getGoodsList,"",String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        //取不到，则手动渲染，再保存到redis
        List<GoodsVo> goodsVoList = goodsService.getGoodsVoList();
        model.addAttribute("goodsList",goodsVoList);
        SpringWebContext ctx = new SpringWebContext(request,response,request.getServletContext(),
                                                    request.getLocale(), model.asMap(),applicationContext);
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list",ctx);
        if(!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsList,"",html);
        }
        return html;
    }

//    @RequestMapping(value = "/to_detail/{goodsId}",produces = "text/html")
//    @ResponseBody
//    public String toDetail(@PathVariable("goodsId") long goodsId,Model model, MiaoshaUser user, HttpServletRequest request,
//                           HttpServletResponse response) throws IOException{
//        if(user == null){
//            response.sendRedirect("/login/to_login");
//            return null;
//        }
//        model.addAttribute("user",user);
//
//        //先尝试从缓存中取
//        String html = redisService.get(GoodsKey.getGoodsDetail,""+goodsId,String.class);
//        if(!StringUtils.isEmpty(html)){
//            return html;
//        }
//
//        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
//        model.addAttribute("goods",goodsVo);
//        long startAt = goodsVo.getStartDate().getTime();
//        long endAt = goodsVo.getEndDate().getTime();
//        long now = System.currentTimeMillis();
//        int miaoshaStatus = 0;//秒杀活动的状态，0-秒杀前；1-正在秒杀；2-秒杀结束
//        int remainSeconds = 0;//秒杀活动还剩多少秒
//        if(now < startAt){
//            miaoshaStatus = Constants.MiaoshaStatus.BEFORE_START;
//            remainSeconds = (int)(startAt-now)/1000;
//        }else if (now > endAt){
//            miaoshaStatus = Constants.MiaoshaStatus.AFTER_MIAOSHA;
//            remainSeconds = -1;
//        }else {
//            miaoshaStatus = Constants.MiaoshaStatus.ON_MIAOSHA;
//            remainSeconds = 0;
//        }
//
//        model.addAttribute("miaoshaStatus",miaoshaStatus);
//        model.addAttribute("remainSeconds",remainSeconds);
//
//        SpringWebContext ctx = new SpringWebContext(request,response,request.getServletContext(),
//                request.getLocale(), model.asMap(),applicationContext);
//        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail",ctx);
//        if(!StringUtils.isEmpty(html)){
//            redisService.set(GoodsKey.getGoodsDetail,""+goodsId,html);
//        }
//        return html;
//    }


    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<DetailVo> toDetail(@PathVariable("goodsId") long goodsId, MiaoshaUser user, HttpServletRequest request,
                                     HttpServletResponse response) throws IOException{
        if(user == null){
            response.sendRedirect("/login/to_login");
            return null;
        }

        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);

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

        DetailVo detailVo = new DetailVo();
        detailVo.setUser(user);
        detailVo.setGoods(goodsVo);
        detailVo.setMiaoshaStatus(miaoshaStatus);
        detailVo.setRemainSeconds(remainSeconds);
        return Result.success(detailVo);
    }

}
