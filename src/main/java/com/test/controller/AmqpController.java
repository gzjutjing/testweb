package com.test.controller;

import com.test.domain.Users;
import com.test.utils.DateUtil;
import configuration.RabbitMqConfig;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * Created by admin on 2017/1/23.
 */
@Controller
public class AmqpController {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitTemplate fanoutTemplate;

    /**
     * @return
     */
    @RequestMapping("/amqpSend")
    @ResponseBody
    public void amqp() {
        Users users = new Users();
        users.setPassword("pwd12355");
        users.setUserId("user id");
        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_DIRECT,
                RabbitMqConfig.RABBITMQ_DEFAULT_ROUTING_KEY, users);
        System.out.println("--------------发送成功");
    }


    @RequestMapping("/mqFanoutSend")
    @ResponseBody
    public String fanout() {
        String t = DateUtil.defaultFormat(new Date());Users u=new Users();
        u.setUserId("admin");
        fanoutTemplate.convertAndSend(u, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setHeader("headerKey", "this is header,time:" + t);
                return message;
            }
        });
        return "成功";
    }
}
