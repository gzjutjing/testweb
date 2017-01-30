package configuration;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by admin on 2017/1/23.
 */
@Configuration
@EnableRabbit
public class RabbitMqConfig {

    private static final String DEFAULT_QUEUE_NAME = "test-queue";

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setPort(AMQP.PROTOCOL.PORT);//5672
        return connectionFactory;
    }

    ///@Bean
    public Queue queue() {
        Queue q = new Queue(DEFAULT_QUEUE_NAME);
        return q;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate();
        template.setConnectionFactory(connectionFactory());
        template.setQueue(DEFAULT_QUEUE_NAME);
        template.setRoutingKey(DEFAULT_QUEUE_NAME);
        return template;
    }

}
