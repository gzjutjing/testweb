package configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.Method;

/**
 * Created by admin on 2016/4/30.
 */
@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Autowired
    private Environment env;

    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        return new RedisCacheManager(redisTemplate);
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
        connectionFactory.setUsePool(true);
        connectionFactory.setHostName(env.getProperty("redis.host.name"));
        connectionFactory.setPassword(env.getProperty("redis.password"));
        connectionFactory.setPort(Integer.valueOf(env.getProperty("redis.port", "6379")));
        connectionFactory.setTimeout(Integer.valueOf(env.getProperty("redis.timeout", "60")));
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(env.getProperty("redis.maxIdle", Integer.class, 10));
        jedisPoolConfig.setMaxTotal(env.getProperty("redis.maxTotal", Integer.class, 100));
        jedisPoolConfig.setMaxWaitMillis(env.getProperty("redis.maxWaitMillis", Long.class, 10000l));
        jedisPoolConfig.setTestOnBorrow(env.getProperty("redis.testOnBorrow", Boolean.class, true));
        connectionFactory.setPoolConfig(jedisPoolConfig);
        return connectionFactory;
    }

    /*@Bean
    public StringRedisTemplate redisTemplate() {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory());
        return template;
    }*/

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate redisTemplate=new RedisTemplate();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public KeyGenerator commonKeyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object p : params) {
                    sb.append(p);
                }
                return sb.toString();
            }
        };
    }
}
