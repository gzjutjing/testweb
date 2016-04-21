package configuration;

import org.springframework.context.annotation.*;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

/**
 * Created by admin on 2016/4/14.
 */
@Configuration
@EnableAspectJAutoProxy
@EnableWebMvc
@ComponentScan(basePackages = "com.test")
@PropertySource(value = "classpath:/config/config.properties")
public class WebConfig {

    @Bean
    public ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver viewResolver=new ThymeleafViewResolver();
//        viewResolver.setsetPrefix("/templates/");
//        viewResolver.setSuffix(".html");
        //viewResolver.setExposeContextBeansAsAttributes(true);
        viewResolver.setCharacterEncoding("utf-8");
        //web容器
        ServletContextTemplateResolver resolver=new ServletContextTemplateResolver();
        resolver.setSuffix(".html");
        resolver.setPrefix("/WEB-INF/templates/");
        resolver.setTemplateMode("XHTML");

        //spring boot使用
        ClassLoaderTemplateResolver resolver1=new ClassLoaderTemplateResolver();
        resolver1.setSuffix(".html");
        resolver1.setPrefix("/");

        SpringTemplateEngine springTemplateEngine=new SpringTemplateEngine();
        springTemplateEngine.addTemplateResolver(resolver);
        springTemplateEngine.addTemplateResolver(resolver1);
        viewResolver.setTemplateEngine(springTemplateEngine);
        return viewResolver;
    }
}
