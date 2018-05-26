package com.swg.miaosha.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * @Author 【swg】.
 * @Date 2018/5/26 14:48
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@Service
@Slf4j
public class MQReceiver {

    @RabbitListener(queues = MQConfig.DIRECT_QUEUE_NAME)
    public void receive(String message){
        log.info("receive:{}",message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE_NAME1)
    public void receiveTopic1(String message){
        log.info("topic queue1 receive:{}",message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE_NAME2)
    public void receiveTopic2(String message){
        log.info("topic queue2 receive:{}",message);
    }

    @RabbitListener(queues = MQConfig.FANOUT_QUEUE_NAME1)
    public void receiveFanout1(String message){
        log.info("fanout queue1 receive:{}",message);
    }

    @RabbitListener(queues = MQConfig.FANOUT_QUEUE_NAME2)
    public void receiveFanout2(String message){
        log.info("fanout queue2 receive:{}",message);
    }

    @RabbitListener(queues = MQConfig.HEADERS_QUEUE_NAME)
    public void receiveHeaders(byte[] message){
        log.info("fanout queue2 receive:{}",new String(message));
    }
}
