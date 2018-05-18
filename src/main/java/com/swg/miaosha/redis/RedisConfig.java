package com.swg.miaosha.redis;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class RedisConfig {
	/*****redis config start*******/
	@Value("${redis.host}")
	private String redisHost;
	@Value("${redis.port}")
	private int redisPort;
	@Value("${redis.max-idle}")
	private int redisMaxTotal;
	@Value("${redis.max-total}")
	private int redisMaxIdle;
	@Value("${redis.max-wait-millis}")
	private int redisMaxWaitMillis;
	/*****redis config end*******/
}
