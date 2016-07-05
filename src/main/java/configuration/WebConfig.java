package configuration;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.DynamicDestinationResolver;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.servlet.config.annotation.*;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.jms.ConnectionFactory;
import java.util.List;

/**
 * Created by admin on 2016/4/14.
 */
@Configuration
@EnableAspectJAutoProxy
@EnableWebMvc
@EnableScheduling
@EnableJms
@EnableAsync
@ComponentScan(basePackages = "com.test")
@Import({PropertiesConfig.class, DBConfig.class, RedisCacheConfig.class, SecurityConfig.class})
//@ImportResource({"classpath*:config/dubbo-client.xml"})
public class WebConfig extends WebMvcConfigurerAdapter {
    @Value("${jms.server.url}")
    private String jmsServerUrl;
    private static final String STATIC_RESOURCES_PRE = "classpath:";

    //--------------rmi start
    /*@Bean
    public RmiProxyFactoryBean rmiProxyFactoryBean(){
        RmiProxyFactoryBean bean=new RmiProxyFactoryBean();
        bean.setServiceInterface(TestDubboService.class);
        bean.setServiceUrl("rmi://127.0.0.1:9999/testRmi");
        return bean;
    }
    //--------------rmi end
    ///hessian
    @Bean
    public HessianProxyFactoryBean testHessian1Service(){
        HessianProxyFactoryBean hessianProxyFactoryBean=new HessianProxyFactoryBean();
        hessianProxyFactoryBean.setServiceInterface(TestHessian1Service.class);
        hessianProxyFactoryBean.setServiceUrl("http://127.0.0.1:8080/testHessian1");
        return hessianProxyFactoryBean;
    }
    @Bean
    public HessianProxyFactoryBean testHessian2Service(){
        HessianProxyFactoryBean hessianProxyFactoryBean=new HessianProxyFactoryBean();
        hessianProxyFactoryBean.setServiceInterface(TestHessian2Service.class);
        hessianProxyFactoryBean.setServiceUrl("http://127.0.0.1:8080/testHessian2");
        return hessianProxyFactoryBean;
    }*/
    ///hessian
    //--------------异步start
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        super.configureAsyncSupport(configurer);
        configurer.setDefaultTimeout(5 * 1000);
        configurer.setTaskExecutor(threadPoolTaskExecutor());
    }

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(25);
        //线程池维护线程的最少数量
        taskExecutor.setCorePoolSize(10);
        //线程池所使用的缓冲队列
        taskExecutor.setQueueCapacity(100);
        taskExecutor.initialize();
        return taskExecutor;
    }
    //--------------异步over

    //静态资源
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/statics/**").addResourceLocations(STATIC_RESOURCES_PRE + "/statics/");
        registry.addResourceHandler("/js/**").addResourceLocations(STATIC_RESOURCES_PRE + "/statics/js/");
        registry.addResourceHandler("/img/**").addResourceLocations(STATIC_RESOURCES_PRE + "/statics/img/");
    }

    @Bean
    public AsyncRestTemplate asyncRestTemplate() {
        return new AsyncRestTemplate();
    }

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
        resolver.setCharacterEncoding("utf-8");
        resolver.setCacheable(true);
        resolver.setCacheTTLMs(10 * 1000l);//10秒

        //spring boot使用
        ClassLoaderTemplateResolver resolver1 = new ClassLoaderTemplateResolver();
        resolver1.setSuffix(".html");
        resolver1.setCharacterEncoding("utf-8");
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

    @Bean
    public JmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory d = new DefaultJmsListenerContainerFactory();
        d.setConnectionFactory(connectionFactory());
        return d;
    }

    @Bean
    public DestinationResolver destinationResolver() {
        return new DynamicDestinationResolver();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
        stringConverter.setWriteAcceptCharset(false);
        converters.add(stringConverter);
        converters.add(new MappingJackson2HttpMessageConverter());
    }
}
