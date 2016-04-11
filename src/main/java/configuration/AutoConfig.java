package configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by admin on 2016/4/11.
 */
@Configuration
@ComponentScan(basePackages = "com.test")
@PropertySource(value = "classpath:/config/config.properties")
public class AutoConfig {
}
