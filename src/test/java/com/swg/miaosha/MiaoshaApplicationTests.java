package com.swg.miaosha;

import com.swg.miaosha.common.CommonCacheUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MiaoshaApplicationTests {

	@Autowired
	private CommonCacheUtil commonCacheUtil;

	@Test
	public void contextLoads() {
	}

	@Test
	public void setRedis(){
		commonCacheUtil.cache("hello","world");
		System.out.println(commonCacheUtil.getCacheValue("hello"));
	}
}
