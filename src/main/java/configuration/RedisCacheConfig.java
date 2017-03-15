package configuration;

import configuration.jodis.JodisConnectionFactory;
import configuration.jodis.JodisPool;
import configuration.jodis.JodisPooledObjectFactory;
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
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.util.ClassUtils;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

/**
 * Created by admin on 2016/4/30.
 */
@Configuration
@EnableCaching(proxyTargetClass = true)
public class RedisCacheConfig {

    @Autowired
    private Environment env;

    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        return new RedisCacheManager(redisTemplate);
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        //return jodisConnectionFactory();
        return jedisConnectionFactory();
    }

    //spring redis
    private JedisConnectionFactory jedisConnectionFactory() {
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

    //jodis
    private JodisConnectionFactory jodisConnectionFactory() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(env.getProperty("redis.maxIdle", Integer.class, 10));
        jedisPoolConfig.setMaxTotal(env.getProperty("redis.maxTotal", Integer.class, 100));
        jedisPoolConfig.setMaxWaitMillis(env.getProperty("redis.maxWaitMillis", Long.class, 10000l));
        jedisPoolConfig.setTestOnBorrow(env.getProperty("redis.testOnBorrow", Boolean.class, true));
        ////////////////////////////
        JodisPooledObjectFactory jodisPooledObjectFactory = new JodisPooledObjectFactory();
        jodisPooledObjectFactory.setZkServers(env.getProperty("codis.zk.servers"));
        jodisPooledObjectFactory.setZkCodisProxyDir(env.getProperty("codis.proxy.dir"));
        jodisPooledObjectFactory.setCodisPassword(env.getProperty("codis.password"));
        jodisPooledObjectFactory.init();
        JodisPool pool = new JodisPool(jedisPoolConfig, jodisPooledObjectFactory);
        JodisConnectionFactory connectionFactory = new JodisConnectionFactory();
        connectionFactory.setPool(pool);
        return connectionFactory;
    }

    /*@Bean
    public StringRedisTemplate redisTemplate() {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory());
        return template;
    }*/

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(connectionFactory);
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 基于redis的分布式计数器，全局唯一
     *
     * @return
     */
    @Bean
    public RedisAtomicLong redisAtomicLong() {
        return new RedisAtomicLong("redisCounter", redisConnectionFactory());
    }

    /**
     * 用redis做cache时key的生成策略
     *
     * @return
     */
    @Bean
    public KeyGenerator commonKeyGenerator() {
        return new KeyGenerator() {
            // custom cache key
            public static final int NO_PARAM_KEY = 0;
            public static final int NULL_PARAM_KEY = 53;

            @Override
            public Object generate(Object target, Method method, Object... params) {

                StringBuilder key = new StringBuilder();
                key.append(target.getClass().getSimpleName()).append(".").append(method.getName()).append(":");
                if (params.length == 0) {
                    return key.append(NO_PARAM_KEY).toString();
                }
                for (Object param : params) {
                    if (param == null) {
                        //log.warn("input null param for Spring cache, use default key={}", NULL_PARAM_KEY);
                        key.append(NULL_PARAM_KEY);
                    } else if (org.springframework.util.ClassUtils.isPrimitiveArray(param.getClass())) {
                        int length = Array.getLength(param);
                        for (int i = 0; i < length; i++) {
                            key.append(Array.get(param, i));
                            key.append(',');
                        }
                    } else if (ClassUtils.isPrimitiveOrWrapper(param.getClass()) || param instanceof String) {
                        key.append(param);
                    } else {
//                        log.warn("Using an object as a cache key may lead to unexpected results. " +
//                                "Either use @Cacheable(key=..) or implement CacheKey. Method is " + target.getClass() + "#" + method.getName());
                        key.append(param.hashCode());
                    }
                    key.append('-');
                }

                String finalKey = key.toString();
                long cacheKeyHash = com.google.common.hash.Hashing.murmur3_128().hashString(finalKey, Charset.defaultCharset()).asLong();
                //log.debug("using cache key={} hashCode={}", finalKey, cacheKeyHash);
                return key.toString();
            }
        };
    }
}
