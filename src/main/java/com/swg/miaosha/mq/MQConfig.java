package com.swg.miaosha.mq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


/**
 * @Author 【swg】.
 * @Date 2018/5/26 14:49
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@Configuration
public class MQConfig {
    public static final String MIAOSHA_QUEUE = "miaoshaqueue";

    @Bean
    public Queue queue(){
        return new Queue(MIAOSHA_QUEUE,true);
    }

}
