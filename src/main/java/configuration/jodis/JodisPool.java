package configuration.jodis;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

/**
 * Created by admin on 2016/8/10.
 */
public class JodisPool extends Pool<Jedis> {

    public JodisPool(GenericObjectPoolConfig poolConfig, PooledObjectFactory<Jedis> factory) {
        super(poolConfig, factory);
    }

}
