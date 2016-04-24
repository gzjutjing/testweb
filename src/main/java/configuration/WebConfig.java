package configuration;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.CachingDestinationResolver;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.DynamicDestinationResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.jms.ConnectionFactory;

/**
 * Created by admin on 2016/4/14.
 */
@Configuration
@EnableAspectJAutoProxy
@EnableWebMvc
@EnableJms
@ComponentScan(basePackages = "com.test")
@PropertySource(value = "classpath:/config/config.properties")
public class WebConfig {
    @Value("${jms.server.url}")
    private String jmsServerUrl;

    ///////////////////////////////////////////////////////////
    //spring boot 和tomcat容器同时启动共用
    @Bean
    public ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
//        viewResolver.setsetPrefix("/templates/");
//        viewResolver.setSuffix(".html");
        //viewResolver.setExposeContextBeansAsAttributes(true);
        viewResolver.setCharacterEncoding("utf-8");
        //web容器
        ServletContextTemplateResolver resolver = new ServletContextTemplateResolver();
        resolver.setSuffix(".html");
        resolver.setPrefix("/WEB-INF/classes/templates/");
        resolver.setTemplateMode("XHTML");

        //spring boot使用
        ClassLoaderTemplateResolver resolver1 = new ClassLoaderTemplateResolver();
        resolver1.setSuffix(".html");
        resolver1.setPrefix("/");

        SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();
        springTemplateEngine.addTemplateResolver(resolver);
        springTemplateEngine.addTemplateResolver(resolver1);
        viewResolver.setTemplateEngine(springTemplateEngine);
        return viewResolver;
    }

    /////////////////////////////////////////////////消息
    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory());
        jmsTemplate.setDefaultDestinationName("spitter.queue");
        return jmsTemplate;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory conn = new ActiveMQConnectionFactory(jmsServerUrl);
        conn.setTrustAllPackages(true);
        return conn;
    }
/*
    @Bean
    public JmsListenerContainerFactory jmsListenerContainerFactory(){
        DefaultJmsListenerContainerFactory d=new DefaultJmsListenerContainerFactory();
        d.setConnectionFactory(connectionFactory());
        return d;
    }
    @Bean
    public DestinationResolver destinationResolver(){
        return new DynamicDestinationResolver();
    }
*/

}
