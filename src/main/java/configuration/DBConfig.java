package configuration;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.hibernate.SessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Administrator on 2016/4/25.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.test.jpa")
@Import({MapperScannerConfig.class, DataSqlInitConfig.class})
public class DBConfig {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Environment environment;

    @Value("${jdbc.url}")
    private String jdbcUrl;
    @Value("${jdbc.username}")
    private String jdbcUsername;
    @Value("${jdbc.password}")
    private String jdbcPassword;

    //jpa start----------------------------------------------------------------------------------------------------------

    /**
     * JPA 实体管理工厂
     */
    @Bean
    public EntityManagerFactory entityManagerFactory() throws SQLException {
        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setDataSource(dataSource());
        bean.setJpaDialect(new HibernateJpaDialect());
        //bean.setPersistenceProvider(new hibernateper);
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.MYSQL);
        adapter.setShowSql(true);
        adapter.setGenerateDdl(true);
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQLDialect");
        bean.setJpaVendorAdapter(adapter);
        bean.setPackagesToScan("com.test.domain");
        Map<String, Object> map = new HashMap<>();
        map.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        map.put("hibernate.ejb.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy");
//        map.put("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");
        map.put("hibernate.show_sql", "true");
        map.put("hibernate.format_sql", "true");
        map.put("hibernate.hbm2ddl.auto", "update");
        map.put("hibernate.schemaUpdate", true);//不配置的话数据库不会更新
        bean.setJpaPropertyMap(map);
        bean.afterPropertiesSet();//不可以少，不然会nullpoint
        return bean.getObject();
    }

    /**
     * JPA 事物处理
     */
    @Bean(name = "jpaTransactionManager")
    public JpaTransactionManager jpaTransactionManager() throws SQLException {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory());
        return jpaTransactionManager;
    }

    /**
     * 事物异常处理
     */
    @Bean
    public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
    //jpa end-----------------------------------------------------------------------------------------------------------

    @Bean
    public JdbcTemplate jdbcTemplate() throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource());
        return jdbcTemplate;
    }

    @Profile("mysql")
    @Bean(name = "dataSource", initMethod = "init", destroyMethod = "close")
    public DataSource dataSource() throws SQLException {
        logger.debug("----------配置datasource信息开始");
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
        logger.debug("----------配置datasource信息结束");
        return druidDataSource;
    }

    @Profile("mysql")
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        logger.debug("----------配置sqlSessionFactory信息开始");
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource());
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactory.setMapperLocations(resolver.getResources("classpath:/mapper/*.xml"));
        logger.debug("----------配置sqlSessionFactory信息结束");
        return sqlSessionFactory.getObject();
    }

    //------------------------------------------------------------------------------------------------------------------
    @Profile("hibernate")
    @Bean
    public LocalSessionFactoryBean sessionFactory() throws SQLException {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(hibernateDataSource());
        sessionFactory.setPackagesToScan(new String[]{"com.test.domain"});
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    @Profile("hibernate")
    @Bean
    public DataSource hibernateDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(environment.getRequiredProperty("jdbc.driverClassName"));
        dataSource.setUrl(environment.getRequiredProperty("jdbc.url"));
        dataSource.setUsername(environment.getRequiredProperty("jdbc.username"));
        dataSource.setPassword(environment.getRequiredProperty("jdbc.password"));
        return dataSource;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
        properties.put("hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"));
        properties.put("hibernate.format_sql", environment.getRequiredProperty("hibernate.format_sql"));
        return properties;
    }

    @Profile("hibernate")
    @Bean
    @Autowired
    public HibernateTransactionManager transactionManager(SessionFactory s) {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(s);
        return txManager;
    }

    //////////////////////事务
    /*@Profile("mysql")
    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }*/

    //------------------------------------------------------------------------------------------------------------------
    //其他
    @Profile("dbcp")
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

    @Profile("jndi")
    @Bean
    public DataSource jndiDataSource() {
        JndiObjectFactoryBean jndi = new JndiObjectFactoryBean();
        jndi.setJndiName("jdbc/testDB");
        jndi.setResourceRef(true);
        jndi.setProxyInterface(DataSource.class);
        return (DataSource) jndi.getObject();
    }

    @Profile("springDB")
    @Bean
    public DataSource embeddedDataSource() {
        EmbeddedDatabaseBuilder build = new EmbeddedDatabaseBuilder();
        build.setType(EmbeddedDatabaseType.HSQL);
        build.addScript("classpath:schema.sql");
        return build.build();
    }
}
