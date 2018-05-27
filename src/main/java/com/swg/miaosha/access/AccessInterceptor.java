package com.swg.miaosha.access;

import com.alibaba.fastjson.JSON;
import com.swg.miaosha.key.AccessKey;
import com.swg.miaosha.model.MiaoshaUser;
import com.swg.miaosha.redis.RedisService;
import com.swg.miaosha.result.CodeMsg;
import com.swg.miaosha.result.Result;
import com.swg.miaosha.service.MiaoshaUserService;
import com.swg.miaosha.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * @Author 【swg】.
 * @Date 2018/5/27 14:26
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@Service
public class AccessInterceptor extends HandlerInterceptorAdapter{

    @Autowired
    private MiaoshaUserService userService;
    @Autowired
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            MiaoshaUser user = getUser(request,response);
            //将user信息存放到ThreadLocal中
            UserContext.setUser(user);

            //取注解，没有此注解的话，直接放行
            HandlerMethod hm = (HandlerMethod)handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if(accessLimit == null){
                return true;
            }
            //取出注解中参数的值
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            //判断是否要必须登陆，如要是必须登陆，看user是否为空，为空的话直接返回fasle和给前台
            if(needLogin){
                if(user == null){
                    render(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                key += "_"+user.getId();
            }else{
                //do nothing
            }

            //限制访问次数
            Integer count = redisService.get(AccessKey.withExpire(seconds),key,Integer.class);
            if(count == null){
                redisService.set(AccessKey.withExpire(seconds),key,1);
            }else if(count < maxCount){
                redisService.incr(AccessKey.withExpire(seconds),key);
            }else {
                render(response, CodeMsg.ACCESS_LIMIT_REACH);
                return false;
            }

        }

        return true;
    }

    private void render(HttpServletResponse response, CodeMsg cm) throws Exception{
        response.setContentType("application/json;charset=UTF-8");//防止中文乱码
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(Result.error(cm));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    private MiaoshaUser getUser(HttpServletRequest request, HttpServletResponse response){
        String paramToken = request.getParameter(CookieUtil.COOKIE_NAME);
        String cookieToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)){
            return null;
        }
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        return userService.getByToken(token,response);
    }
}
