package com.swg.miaosha.controller;


import com.swg.miaosha.mq.MQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author 【swg】.
 * @Date 2018/5/26 15:14
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@Controller
@RequestMapping("/test")
public class TestController {
    @Autowired
    private MQSender mqSender;

    @RequestMapping("/mq")
    @ResponseBody
    public String mq(){
        mqSender.send("hello world");
        return "success";
    }

    @RequestMapping("/mq/topic")
    @ResponseBody
    public String mq_topic(){
        mqSender.sendTopic("hello world");
        return "success";
    }

    @RequestMapping("/mq/fanout")
    @ResponseBody
    public String mq_fanout(){
        mqSender.sendFanout("hello world");
        return "success";
    }

    @RequestMapping("/mq/headers")
    @ResponseBody
    public String mq_headers(){
        mqSender.sendHeaders("hello world");
        return "success";
    }
}
