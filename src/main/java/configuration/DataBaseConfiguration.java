package configuration;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jndi.JndiObjectFactoryBean;

import javax.sql.DataSource;

/**
 * Created by Administrator on 2016/4/25.
 */
@Configuration
public class DataBaseConfiguration {

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
