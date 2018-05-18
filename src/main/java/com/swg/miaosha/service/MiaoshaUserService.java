package com.swg.miaosha.service;

import com.swg.miaosha.dao.MiaoshaUserDao;
import com.swg.miaosha.exception.GlobalException;
import com.swg.miaosha.key.MiaoshaUserKey;
import com.swg.miaosha.model.MiaoshaUser;
import com.swg.miaosha.redis.RedisService;
import com.swg.miaosha.result.CodeMsg;
import com.swg.miaosha.util.CookieUtil;
import com.swg.miaosha.util.MD5Util;
import com.swg.miaosha.util.UUIDUtil;
import com.swg.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoshaUserService {
	@Autowired
    private RedisService redisService;
	
	@Autowired
	MiaoshaUserDao miaoshaUserDao;

	public MiaoshaUser getById(long id){
		return miaoshaUserDao.getById(id);
	}

	public boolean login(LoginVo loginVo,HttpServletResponse response) {
		if (loginVo == null)
			throw new GlobalException(CodeMsg.SERVER_ERROR);
		String mobile = loginVo.getMobile();
		String password = loginVo.getPassword();

		//判断手机号码是否存在
		MiaoshaUser user = getById(Long.parseLong(mobile));
		if(user == null){
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}

		//验证密码是否匹配
		String dbPass = user.getPassword();
		String dbSalt = user.getSalt();
		if(!MD5Util.formPassToDBPass(password,dbSalt).equals(dbPass)){
			throw new GlobalException(CodeMsg.PASSWORD_ERROR);
		}

		//生成cookie
		String token = UUIDUtil.uuid();
        redisService.set(MiaoshaUserKey.token,token,user);//key--->UserKey:tkUUID,value--->Serialized User
        CookieUtil.writeLoginToken(response,token);


		return true;
	}

    public MiaoshaUser getByToken(String token,HttpServletResponse response) {
        if(StringUtils.isEmpty(token)){
            return null;
        }
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token,token,MiaoshaUser.class);
        redisService.set(MiaoshaUserKey.token,token,user);//key--->UserKey:tkUUID,value--->Serialized User
        if(user != null){
            redisService.set(MiaoshaUserKey.token,token,user);
        }
        return user;
    }
}
