package com.test.controller;

import com.test.domain.Users;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by admin on 2017/1/23.
 */
@Controller
public class AmqpController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * @return
     */
    @RequestMapping("/amqpSend")
    @ResponseBody
    public void amqp() {
        Users users = new Users();
        users.setPassword("12355");
        users.setUserId("admin");
        rabbitTemplate.convertAndSend(users);
    }

}
