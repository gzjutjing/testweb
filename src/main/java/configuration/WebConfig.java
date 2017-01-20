package configuration;

import com.test.exception.AsyncRejectException;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
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
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.jms.ConnectionFactory;
import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by admin on 2016/4/14.
 */
@Configuration
@EnableAspectJAutoProxy
@EnableWebMvc
@EnableScheduling
@EnableJms
@EnableAsync
@EnableJpaRepositories("com.test.mapper")
@ComponentScan(basePackages = "com.test")
@Import({PropertiesConfig.class, DBConfig.class, RedisCacheConfig.class, SwaggerConfig.class})//, SecurityConfig.class
//@ImportResource({"classpath*:config/dubbo-client.xml"})
public class WebConfig extends WebMvcConfigurerAdapter {
    private Logger logger = LoggerFactory.getLogger(WebConfig.class);
    @Value("${jms.server.url}")
    private String jmsServerUrl;
    private static final String STATIC_RESOURCES_PRE = "classpath:";
    @Autowired
    private ApplicationContext applicationContext;

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
        taskExecutor.setMaxPoolSize(200);
        //线程池维护线程的最少数量
        taskExecutor.setCorePoolSize(10);
        //线程池所使用的缓冲队列
        taskExecutor.setQueueCapacity(5000);
        taskExecutor.setThreadNamePrefix("spring异步请求线程");
        taskExecutor.setKeepAliveSeconds(5000);
        //@TODO 此配置无效，max pool也无效，曹国core pool就挂了，后期处理
        RejectedExecutionHandler rejectedExecutionHandler = new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                logger.error("------------------------异步处理失败，重新执行-----------------------");
                //打印线程池的对象
                logger.debug("The pool RejectedExecutionHandler = " + executor.toString());
                throw new AsyncRejectException();
            }
        };
        //taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.setRejectedExecutionHandler(rejectedExecutionHandler);
        taskExecutor.initialize();
        return taskExecutor;
    }
    //--------------异步over

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

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
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(applicationContext);
        resolver.setSuffix(".html");
        resolver.setPrefix("/WEB-INF/classes/templates/");
        resolver.setTemplateMode("XHTML");
        resolver.setCharacterEncoding("utf-8");
        resolver.setCacheable(true);
        resolver.setCacheTTLMs(1 * 1000l);//10秒

        //spring boot使用
        ClassLoaderTemplateResolver resolver1 = new ClassLoaderTemplateResolver();
        resolver1.setSuffix(".html");
        resolver1.setCharacterEncoding("utf-8");
        resolver1.setPrefix("/");

        SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();
        springTemplateEngine.addTemplateResolver(resolver);
        springTemplateEngine.addTemplateResolver(resolver1);
        //thymeleaf自动加载csrf hidden字段
        springTemplateEngine.addDialect(new SpringSecurityDialect());
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
