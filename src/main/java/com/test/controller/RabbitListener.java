package com.test.controller;

import com.test.domain.Users;
import configuration.RabbitMqConfig;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

/**
 * Created by admin on 2017/2/1.
 */
@Controller
public class RabbitListener {
    @org.springframework.amqp.rabbit.annotation.RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = RabbitMqConfig.QUEUE_DIRECT_SYNC, durable = "true"),
                    exchange = @Exchange(value = RabbitMqConfig.EXCHANGE_DIRECT, ignoreDeclarationExceptions = "true"),
                    key = RabbitMqConfig.RABBITMQ_DEFAULT_ROUTING_KEY
            )
    )
    public void directHandler(@Payload Users users) {
        System.out.println("----+----");
        System.out.println("rabbit handler:" + users.getUserId());
    }

    //////////////////////////////////////////////////////////////////////////廣播
    @org.springframework.amqp.rabbit.annotation.RabbitListener(
            queues = RabbitMqConfig.QUEUE_FANOUT_1
    )
    public void fanout1(Users t, @Header(name = "headerKey") String headerKey) {
        System.out.println("================================1");
        System.out.println("rabbit handler string:" + t.getUserId());
        System.out.println("fanout1 headerKey:" + headerKey);
        System.out.println("================================1");
    }

    @org.springframework.amqp.rabbit.annotation.RabbitListener(
            queues = RabbitMqConfig.QUEUE_FANOUT_1
    )
    public void fanout2(Users t, @Header(name = "headerKey") String headerKey) {
        System.out.println("================================2");
        System.out.println("rabbit handler string:" + t.getUserId());
        System.out.println("fanout2 headerKey:" + headerKey);
        System.out.println("================================2");
    }
}
