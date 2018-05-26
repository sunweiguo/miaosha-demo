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
    //direct MQ name
    public static final String DIRECT_QUEUE_NAME = "queue";
    //Topic MQ
    public static final String TOPIC_QUEUE_NAME1 = "topic.queue1";
    public static final String TOPIC_QUEUE_NAME2 = "topic.queue2";
    public static final String TOPIC_EXCHANGE_NAME = "topicExchange";
    private static final String TOPIC_KEY_ROUTE1 = "topic.key1";
    private static final String TOPIC_KEY_ROUTE2 = "topic.#";



    //fanout
    public static final String FANOUT_EXCHANGE_NAME = "fanoutExchage";
    public static final String FANOUT_QUEUE_NAME1 = "fanout.queue1";
    public static final String FANOUT_QUEUE_NAME2 = "fanout.queue2";

    /*headers*/
    public static final String HEADERS_EXCHANGE_NAME = "headersExchage";
    public static final String HEADERS_QUEUE_NAME = "headers.queue";

    /*direct*/
    @Bean
    public Queue queue(){
        return new Queue(DIRECT_QUEUE_NAME,true);
    }

    /*topic*/
    @Bean
    public Queue topicQueue1(){
        return new Queue(TOPIC_QUEUE_NAME1,true);
    }
    @Bean
    public Queue topicQueue2(){
        return new Queue(TOPIC_QUEUE_NAME2,true);
    }
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(TOPIC_EXCHANGE_NAME);
    }
    @Bean
    public Binding topicBinding1(){
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(TOPIC_KEY_ROUTE1);
    }
    @Bean
    public Binding topicBinding2(){
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(TOPIC_KEY_ROUTE2);
    }

    /*fanout*/
    @Bean
    public Queue fanoutQueue1(){
        return new Queue(FANOUT_QUEUE_NAME1,true);
    }
    @Bean
    public Queue fanoutQueue2(){
        return new Queue(FANOUT_QUEUE_NAME2,true);
    }
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(FANOUT_EXCHANGE_NAME);
    }
    @Bean
    public Binding fanoutBinding1(){
        return BindingBuilder.bind(fanoutQueue1()).to(fanoutExchange());
    }
    @Bean
    public Binding fanoutBinding2(){
        return BindingBuilder.bind(fanoutQueue2()).to(fanoutExchange());
    }



    /*headers*/
    @Bean
    public HeadersExchange headersExchange(){
        return new HeadersExchange(HEADERS_EXCHANGE_NAME);
    }
    @Bean
    public Queue headersQueue(){
        return new Queue(HEADERS_QUEUE_NAME,true);
    }
    @Bean
    public Binding headersBinding(){
        Map<String,Object> map = new HashMap<>();
        map.put("header1","value1");
        map.put("header2","value2");
        return BindingBuilder.bind(headersQueue()).to(headersExchange()).whereAll(map).match();
    }
}
