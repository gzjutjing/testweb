package configuration;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Created by admin on 2017/1/23.
 */
@Configuration
@EnableRabbit
public class RabbitMqConfig {

    public static final String QUEUE_DIRECT_SYNC = "ccs.queue.sync";
    public static final String QUEUE_DIRECT_ASYNC = "ccs.queue.async";
    public static final String QUEUE_DIRECT_SYNC_REPLAY = "ccs.queue.sync.reply";

    public static final String QUEUE_FANOUT_1 = "fanout.queue1";
    public static final String QUEUE_FANOUT_2 = "fanout.queue2";

    public static final String EXCHANGE_DIRECT = "rabbitmq.direct.exchange";
    public static final String EXCHANGE_TOPIC = "rabbitmq.topic.exchange";
    public static final String EXCHANGE_FANOUT = "exchange.fanout";

    public static final String RABBITMQ_DEFAULT_ROUTING_KEY = "rabbitmq.default.routing.key";

    @Bean
    public AmqpAdmin amqpAdmin() {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
        rabbitAdmin.afterPropertiesSet();
        return rabbitAdmin;
    }

    @Bean
    public RabbitTransactionManager rabbitTransactionManager() {
        RabbitTransactionManager manager = new RabbitTransactionManager();
        manager.setConnectionFactory(connectionFactory());
        return manager;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setPort(AMQP.PROTOCOL.PORT);//5672
        return connectionFactory;
    }

    //消费者配置
    @Bean
    public RabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory simple = new SimpleRabbitListenerContainerFactory();
        simple.setConnectionFactory(connectionFactory());
        simple.setConcurrentConsumers(3);
        simple.setMaxConcurrentConsumers(10);
        simple.setChannelTransacted(true);//如果它为true,就会告知框架使用事务通道，并根据结果使用提交或回滚来结束所有操作,出现异常时则发出回滚信号.
        simple.setMessageConverter(new Jackson2JsonMessageConverter());
        return simple;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue asyncQueue() {
        Queue q = new Queue(QUEUE_DIRECT_ASYNC);
        return q;
    }

    @Bean
    public Queue syncQueue() {
        return QueueBuilder.durable(QUEUE_DIRECT_SYNC).withArgument("x-message-ddl", 100).build();
    }

    @Bean
    public Queue retryQueue() {
        Queue q = new Queue(QUEUE_DIRECT_SYNC_REPLAY);
        return q;
    }

    @Bean
    public DirectExchange directExchange() {
        DirectExchange directExchange = new DirectExchange(EXCHANGE_DIRECT, true, false);
        return directExchange;
    }

    @Bean
    public Exchange topicExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_TOPIC).durable().withArgument("foo", "bar").build();
    }

    @Bean
    public Binding bindingAsync() {
        return BindingBuilder.bind(asyncQueue()).to(directExchange()).with("RABBITMQ_DEFAULT_ROUTING_KEY");
    }

    @Bean
    public Binding bindingSync() {
        return BindingBuilder.bind(syncQueue()).to(directExchange()).with("RABBITMQ_DEFAULT_ROUTING_KEY");
    }

    @Bean
    public Binding bindingRetry() {
        return BindingBuilder.bind(retryQueue()).to(directExchange()).with("RABBITMQ_DEFAULT_ROUTING_KEY");
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate();
        template.setConnectionFactory(connectionFactory());
        template.setExchange(EXCHANGE_DIRECT);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        template.setRetryTemplate(retryTemplate());
        return template;
    }

    /**
     * 重试
     *
     * @return
     */
    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(500);
        backOffPolicy.setMaxInterval(10000);
        backOffPolicy.setMultiplier(10.0);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        return retryTemplate;
    }

    //////////////////////////////////广播
    @Bean
    public List<Queue> fanoutQueue1() {
        return Arrays.asList(new Queue(QUEUE_FANOUT_1, true, false, true),
                new Queue(QUEUE_FANOUT_2, true, false, true));
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        FanoutExchange fanoutExchange = new FanoutExchange(EXCHANGE_FANOUT, true, true);
        return fanoutExchange;
    }

    @Bean
    public List<Binding> fanout2Binding() {
        return Arrays.asList(
                new Binding(QUEUE_FANOUT_1, Binding.DestinationType.QUEUE, EXCHANGE_FANOUT, RABBITMQ_DEFAULT_ROUTING_KEY, null),
                new Binding(QUEUE_FANOUT_2, Binding.DestinationType.QUEUE, EXCHANGE_FANOUT, RABBITMQ_DEFAULT_ROUTING_KEY, null));
    }

    @Bean
    public RabbitTemplate fanoutTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory());
        rabbitTemplate.setMessageConverter(messageConverter());
        rabbitTemplate.setExchange(EXCHANGE_FANOUT);
        rabbitTemplate.setRetryTemplate(retryTemplate());
        rabbitTemplate.setReplyTimeout(2000);
        rabbitTemplate.setChannelTransacted(true);//如果它为true,就会告知框架使用事务通道，并根据结果使用提交或回滚来结束所有操作,出现异常时则发出回滚信号.
        return rabbitTemplate;
    }
}
