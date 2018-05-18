package com.swg.miaosha.controller;

import com.swg.miaosha.model.MiaoshaUser;
import com.swg.miaosha.service.MiaoshaUserService;
import com.swg.miaosha.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;


@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private MiaoshaUserService userService;

    @RequestMapping("to_list")
    public String toList(@CookieValue(value= CookieUtil.COOKIE_NAME,required = false) String cookieToken,
                       @RequestParam(value = CookieUtil.COOKIE_NAME,required = false) String paramToken,
                         Model model,HttpServletResponse response){
        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)){
            return "login";
        }
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        MiaoshaUser user = userService.getByToken(token,response);
        if(user == null){
            return "login";
        }
        model.addAttribute("user",user);
        return "goods_list";
    }
}
