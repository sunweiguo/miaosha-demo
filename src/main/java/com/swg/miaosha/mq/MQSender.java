package com.swg.miaosha.mq;

import com.swg.miaosha.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author 【swg】.
 * @Date 2018/5/26 14:48
 * @DESC
 * @CONTACT 317758022@qq.com
 */

@Service
@Slf4j
public class MQSender {

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void send(Object message){
        amqpTemplate.convertAndSend(MQConfig.DIRECT_QUEUE_NAME,message);
        log.info("send:{}",message);
    }

    public void sendTopic(Object message){
        log.info("send topic msg:{}",message);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE_NAME,"topic.key1",message+"--1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE_NAME,"topic.key2",message+"--2");

    }


    public void sendFanout(Object message){
        log.info("send fanout msg:{}",message);
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE_NAME,"",message);
    }

    public void sendHeaders(Object message){
        String msg = RedisService.beanToString(message);
        log.info("send fanout msg:{}",message);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("header1","value1");
        properties.setHeader("header2","value2");
        Message obj = new Message(msg.getBytes(),properties);
        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE_NAME,"",obj);
    }

}
