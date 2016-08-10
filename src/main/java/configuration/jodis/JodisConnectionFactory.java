package configuration.jodis;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.PassThroughExceptionTranslationStrategy;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConnection;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConverters;
import org.springframework.jdbc.support.SQLExceptionTranslator;

/**
 * Created by admin on 2016/8/10.
 */
public class JodisConnectionFactory implements RedisConnectionFactory {

    private JodisPool pool;
    private Integer INDEX = 0;
    private boolean convertPipelineAndTxResults = true;

    /**
     * Provides a suitable connection for interacting with Redis.
     *
     * @return connection for interacting with Redis.
     */
    @Override
    public RedisConnection getConnection() {
        JedisConnection jedisConnection = new JedisConnection(pool.getResource(), pool, INDEX);
        jedisConnection.setConvertPipelineAndTxResults(convertPipelineAndTxResults);
        return jedisConnection;
    }

    /**
     * Provides a suitable connection for interacting with Redis Cluster.
     *
     * @return
     * @throws
     * @since 1.7
     */
    @Override
    public RedisClusterConnection getClusterConnection() {
        throw new RuntimeException("unsupport method getClusterConnection!");
    }

    /**
     * Specifies if pipelined results should be converted to the expected data type. If false, results of
     * {@link RedisConnection#closePipeline()} and {RedisConnection#exec()} will be of the type returned by the underlying
     * driver This method is mostly for backwards compatibility with 1.0. It is generally always a good idea to allow
     * results to be converted and deserialized. In fact, this is now the default behavior.
     *
     * @return Whether or not to convert pipeline and tx results
     */
    @Override
    public boolean getConvertPipelineAndTxResults() {
        return convertPipelineAndTxResults;
    }

    /**
     * Provides a suitable connection for interacting with Redis Sentinel.
     *
     * @return connection for interacting with Redis Sentinel.
     * @since 1.4
     */
    @Override
    public RedisSentinelConnection getSentinelConnection() {
        throw new RuntimeException("unsupport method getSentinelConnection!");
    }

    /**
     * Translate the given runtime exception thrown by a persistence framework to a
     * corresponding exception from Spring's generic
     * {@link DataAccessException} hierarchy, if possible.
     * <p>Do not translate exceptions that are not understood by this translator:
     * for example, if coming from another persistence framework, or resulting
     * from user code or otherwise unrelated to persistence.
     * <p>Of particular importance is the correct translation to
     * DataIntegrityViolationException, for example on constraint violation.
     * Implementations may use Spring JDBC's sophisticated exception translation
     * to provide further information in the event of SQLException as a root cause.
     *
     * @param ex a RuntimeException to translate
     * @return the corresponding DataAccessException (or {@code null} if the
     * exception could not be translated, as in this case it may result from
     * user code rather than from an actual persistence problem)
     * @see DataIntegrityViolationException
     * @see SQLExceptionTranslator
     */
    @Override
    public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
        return new PassThroughExceptionTranslationStrategy(
                JedisConverters.exceptionConverter()).translate(ex);
    }

    public boolean isConvertPipelineAndTxResults() {
        return convertPipelineAndTxResults;
    }

    public void setConvertPipelineAndTxResults(boolean convertPipelineAndTxResults) {
        this.convertPipelineAndTxResults = convertPipelineAndTxResults;
    }

    public JodisPool getPool() {
        return pool;
    }

    public void setPool(JodisPool pool) {
        this.pool = pool;
    }
}
