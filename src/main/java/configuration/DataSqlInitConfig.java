package configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * 初始化数据库脚本
 * Created by admin on 2016/5/12.
 */
@Configuration
public class DataSqlInitConfig {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private DataSource dataSource;

    @PostConstruct
    protected void init() {
        logger.debug("-------------开始初始化数据库脚本-------------");
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(resourceLoader.getResource("classpath:test.sql"));
        populator.addScript(new ClassPathResource("data.sql"));
        populator.setContinueOnError(false);
        populator.setSqlScriptEncoding("UTF-8");
        DatabasePopulatorUtils.execute(populator, dataSource);
        logger.debug("-------------结束初始化数据库脚本-------------");
    }
}
