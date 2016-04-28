package configuration;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 单独一个文件
 * Created by admin on 2016/4/28.
 */
@Configuration
public class MapperScannerConfig {
    @Profile("mysql")
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapper = new MapperScannerConfigurer();
        mapper.setSqlSessionFactoryBeanName("sqlSessionFactory");
        mapper.setBasePackage("com.test.mapper");
        return mapper;
    }
}
