package com.swg.miaosha.util;

import com.swg.miaosha.key.MiaoshaUserKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CookieUtil {
    //tomcat8.5版本之前是可以用".oursnail.com"的，后面的版本不能那么写
    //private final static String COOKIE_DOMAIN = "oursnail.com";
    public final static String COOKIE_NAME = "login_token";


    /**
     * 登陆的时候写入cookie
     * @param response
     * @param token
     */
    public static void writeLoginToken(HttpServletResponse response, String token){
        Cookie ck = new Cookie(COOKIE_NAME,token);
        //ck.setDomain(COOKIE_DOMAIN);
        ck.setPath("/");//设值在根目录
        ck.setHttpOnly(true);//不允许通过脚本访问cookie,避免脚本攻击
        ck.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        log.info("write cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
        response.addCookie(ck);
    }

    /**
     * 读取登陆的cookie
     * @param request
     * @return
     */
    public static String readLoginToken(HttpServletRequest request){
        Cookie[] cks = request.getCookies();
        if(cks != null){
            for(Cookie ck:cks){
                log.info("cookieName:{},cookieBValue:{}",ck.getName(),ck.getValue());
                if(StringUtils.equals(ck.getName(),COOKIE_NAME)){
                    log.info("return cookieName:{},cookieBValue:{}",ck.getName(),ck.getValue());
                    return ck.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 注销的时候进行删除
     * @param request
     * @param response
     */
    public static void delLoginToken(HttpServletRequest request,HttpServletResponse response){
        Cookie[] cks = request.getCookies();
        if(cks != null){
            for(Cookie ck:cks) {
                if(StringUtils.equals(ck.getName(),COOKIE_NAME)){
                    //ck.setDomain(COOKIE_DOMAIN);
                    ck.setPath("/");
                    ck.setMaxAge(0);//0表示消除此cookie
                    log.info("del cookieName:{},cookieBValue:{}",ck.getName(),ck.getValue());
                    response.addCookie(ck);
                    return;
                }
            }
        }
    }
}
