package com.swg.miaosha;

import com.swg.miaosha.key.MiaoshaUserKey;
import com.swg.miaosha.model.MiaoshaUser;
import com.swg.miaosha.redis.RedisService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MiaoshaApplicationTests {

	@Autowired
	private RedisService redisService;

	@Test
	public void contextLoads() {
	}

	@Test
	public void setRedis(){
		MiaoshaUser user = new MiaoshaUser();
		user.setId(1L);
		user.setNickname("swg");
		redisService.set(MiaoshaUserKey.token,"hello",user);
	}
}
