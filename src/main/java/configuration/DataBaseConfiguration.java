package configuration;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jndi.JndiObjectFactoryBean;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by Administrator on 2016/4/25.
 */
@Configuration
@MapperScan("com.test.mapper")
public class DataBaseConfiguration {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${jdbc.url}")
    private String jdbcUrl;
    @Value("${jdbc.username}")
    private String jdbcUsername;
    @Value("${jdbc.password}")
    private String jdbcPassword;

    @Profile("used")
    @Bean(initMethod = "init", destroyMethod = "close")
    public DataSource mysqlDataSource() throws SQLException {
        logger.debug("配置datasource信息开始");
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(jdbcUrl);
        druidDataSource.setUsername(jdbcUsername);
        druidDataSource.setPassword(jdbcPassword);
        druidDataSource.setInitialSize(1);
        druidDataSource.setMaxActive(50);
        druidDataSource.setMinIdle(0);
        druidDataSource.setMaxWait(5000);
        //配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
        //配置一个连接在池中最小生存的时间，单位是毫秒
        druidDataSource.setMinEvictableIdleTimeMillis(300000);
        //用来检测连接是否有效的sql，要求是一个查询语句。如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会其作用。
        druidDataSource.setValidationQuery("select 1");
        //建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setDefaultAutoCommit(false);
        //打开PSCache，并且指定每个连接上PSCache的大小
        druidDataSource.setPoolPreparedStatements(true);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
        //自动清除无用连接
        druidDataSource.setRemoveAbandoned(true);
        //清除无用连接的等待时间
        druidDataSource.setRemoveAbandonedTimeout(7200);
        druidDataSource.setFilters("stat");
        logger.debug("配置datasource信息结束");
        return druidDataSource;
    }

    @Profile("used")
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        logger.debug("配置sqlSessionFactory信息开始");
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(mysqlDataSource());
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactory.setMapperLocations(resolver.getResources("classpath:/mapper/*.xml"));
        logger.debug("配置sqlSessionFactory信息结束");
        return sqlSessionFactory.getObject();
    }

    @Profile("dev")
    @Bean
    public DataSource dbcpDataSource() {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName("org.h2.Driver");
        basicDataSource.setUrl("jdbc:h2:tcp://localhost/~/spitter");
        basicDataSource.setUsername("sa");
        basicDataSource.setPassword("");
        basicDataSource.setInitialSize(5);
        basicDataSource.setMaxIdle(10);
        return basicDataSource;
    }

    @Profile("test")
    @Bean
    public DataSource jndiDataSource() {
        JndiObjectFactoryBean jndi = new JndiObjectFactoryBean();
        jndi.setJndiName("jdbc/testDB");
        jndi.setResourceRef(true);
        jndi.setProxyInterface(DataSource.class);
        return (DataSource) jndi.getObject();
    }

    @Profile("spring")
    @Bean
    public DataSource embeddedDataSource() {
        EmbeddedDatabaseBuilder build = new EmbeddedDatabaseBuilder();
        build.setType(EmbeddedDatabaseType.HSQL);
        build.addScript("classpath:schema.sql");
        return build.build();
    }
}
