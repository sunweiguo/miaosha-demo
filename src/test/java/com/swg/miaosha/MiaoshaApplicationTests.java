package com.swg.miaosha;

import com.swg.miaosha.dao.GoodsDao;
import com.swg.miaosha.key.MiaoshaUserKey;
import com.swg.miaosha.model.Goods;
import com.swg.miaosha.model.MiaoshaUser;
import com.swg.miaosha.redis.RedisService;
import com.swg.miaosha.vo.GoodsVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MiaoshaApplicationTests {

	@Autowired
	private GoodsDao goodsDao;

	@Test
	public void contextLoads() {
	}

	@Test
	public void setRedis(){
	}
}
