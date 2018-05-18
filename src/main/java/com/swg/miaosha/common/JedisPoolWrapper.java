package com.swg.miaosha.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class JedisPoolWrapper {
    private JedisPool jedisPool = null;

    @Autowired
    private RedisConfig redisConfig;

    @PostConstruct
    public void init() {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(redisConfig.getRedisMaxTotal());
            config.setMaxIdle(redisConfig.getRedisMaxIdle());
            config.setMaxWaitMillis(redisConfig.getRedisMaxWaitMillis());

            jedisPool = new JedisPool(config,redisConfig.getRedisHost(),redisConfig.getRedisPort(),2000);
        } catch (Exception e) {
            log.error("Fail to initialize jedis pool", e);
        }
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }
}
