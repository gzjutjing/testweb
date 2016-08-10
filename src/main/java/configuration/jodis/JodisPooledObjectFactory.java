package configuration.jodis;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import io.codis.jodis.BoundedExponentialBackoffRetryUntilElapsed;
import io.codis.jodis.CodisProxyInfo;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode.BUILD_INITIAL_CACHE;
import static org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.*;

/**
 * Created by admin on 2016/8/10.
 */
public class JodisPooledObjectFactory implements PooledObjectFactory<Jedis> {

    private static final Logger LOG = LoggerFactory.getLogger(JodisPooledObjectFactory.class);

    public static final int ZK_RETRY_BASE_TIME_MS = 100;

    public static final int ZK_RETRY_MAX_TIME_MS = 100 * 30;

    private static final ImmutableSet<PathChildrenCacheEvent.Type> RESET_TYPES = Sets
            .immutableEnumSet(CHILD_ADDED, CHILD_UPDATED, CHILD_REMOVED);

    private final AtomicInteger nextIdx = new AtomicInteger(-1);

    /**
     * zookeeper server's ip:port,ip:port,....
     */
    private String zkServers;

    private String zkCodisProxyDir;

    private int zkSessionTimeoutMs = 2000;

    private RetryPolicy zkRetryPolicy;

    private int codisConnectionTimeoutMs = 2000;

    private int codisTimeoutMs = 2000;

    private String codisPassword;

    private JedisPoolConfig poolConfig;

    private ImmutableList<HostAndPort> codisNodes;

    private ImmutableSet<String> codisAddrs;

    private PathChildrenCache watcher;

    public JodisPooledObjectFactory() {
        super();
    }

    public void init() {
        /* 初始化pool的config */
        if (poolConfig == null) {
            poolConfig = new JedisPoolConfig();
        }

        /* 初始化 zkClient*/
        Preconditions.checkNotNull(zkServers, "zkServer can not be null");
        Preconditions.checkNotNull(zkCodisProxyDir, "zk codis dir can not be null");
        CuratorFrameworkFactory.Builder clientBuilder = CuratorFrameworkFactory.builder().connectString(zkServers)
                .sessionTimeoutMs(zkSessionTimeoutMs);
        if (zkRetryPolicy == null) {
            clientBuilder.retryPolicy(
                    new BoundedExponentialBackoffRetryUntilElapsed(ZK_RETRY_BASE_TIME_MS, ZK_RETRY_MAX_TIME_MS,
                            Long.MAX_VALUE));
        } else {
            clientBuilder.retryPolicy(zkRetryPolicy);
        }
        CuratorFramework curatorClient = clientBuilder.build();
        curatorClient.start();
        /* 初始化zookeeper的watcher*/
        watcher = new PathChildrenCache(curatorClient, zkCodisProxyDir, true);
        watcher.getListenable().
                addListener(new PathChildrenCacheListener() {
                                private void logEvent(PathChildrenCacheEvent event) {
                                    StringBuilder msg = new StringBuilder("Receive child event: ");
                                    msg.append("type=").append(event.getType());
                                    ChildData data = event.getData();
                                    if (data != null) {
                                        msg.append(", path=").append(data.getPath());
                                        msg.append(", stat=").append(data.getStat());
                                        if (data.getData() != null) {
                                            msg.append(", bytes length=").append(data.getData().length);
                                        } else {
                                            msg.append(", no bytes");
                                        }
                                    } else {
                                        msg.append(", no data");
                                    }
                                    LOG.info(msg.toString());
                                }

                                @Override
                                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                                    logEvent(event);
                                    if (RESET_TYPES.contains(event.getType())) {
                                        renewCodisNodes();
                                    }
                                }
                            }

                );
        try {
            watcher.start(BUILD_INITIAL_CACHE);
            renewCodisNodes();
        } catch (Exception e) {
            LOG.error("init codis zookeeper watch exception：" + zkServers + ":" + zkCodisProxyDir, e);
            throw new JedisException(e);
        }
    }

    private void renewCodisNodes() {
        ImmutableList.Builder<HostAndPort> hostAndPortBuilder = ImmutableList.builder();
        ImmutableSet.Builder<String> codisAddrBuilder = ImmutableSet.builder();

        for (ChildData childData : watcher.getCurrentData()) {
            try {
                CodisProxyInfo proxyInfo = JSONObject.parseObject(childData.getData(), CodisProxyInfo.class);
                if (!"online".equals(proxyInfo.getState())) {
                    continue;
                }
                String addr = proxyInfo.getAddr();
                codisAddrBuilder.add(addr);
                LOG.info("Add new proxy: " + addr);
                String[] addrTokens = addr.split(":");
                String host = addrTokens[0];
                int port = Integer.parseInt(addrTokens[1]);
                hostAndPortBuilder.add(new HostAndPort(host, port));
            } catch (Throwable t) {
                LOG.warn("parse " + childData.getPath() + " failed", t);
            }
        }
        this.codisNodes = hostAndPortBuilder.build();
        this.codisAddrs = codisAddrBuilder.build();
    }

    private HostAndPort getNextHostAndPort() {
        if (codisNodes.isEmpty()) {
            throw new JedisException("Proxy list empty");
        }
        for (; ; ) {
            int current = nextIdx.get();
            int next = current >= codisNodes.size() - 1 ? 0 : current + 1;
            if (nextIdx.compareAndSet(current, next)) {
                return codisNodes.get(next);
            }
        }
    }

    @Override
    public void activateObject(PooledObject<Jedis> pooledCodis) throws Exception {
        // do nothing
    }

    @Override
    public void destroyObject(PooledObject<Jedis> pooledCodis) throws Exception {
        final Jedis codis = pooledCodis.getObject();
        if (codis.isConnected()) {
            try {
                codis.quit();
            } catch (Exception e) {
                LOG.error("close codis exception:" + codis.getClient().getHost() + ":" + codis.getClient().getPort(),
                        e);
            }
            try {
                codis.disconnect();
            } catch (Exception e) {
                LOG.error(
                        "disconnect codis exception:" + codis.getClient().getHost() + ":" + codis.getClient().getPort(), e);
            }
        }
    }

    @Override
    public PooledObject<Jedis> makeObject() throws Exception {
        final HostAndPort hostAndPort = this.getNextHostAndPort();
        final Jedis codis = new Jedis(hostAndPort.getHost(), hostAndPort.getPort(), codisConnectionTimeoutMs,
                codisTimeoutMs);
        try {
            codis.connect();
            if (null != this.codisPassword) {
                codis.auth(this.codisPassword);
            }
        } catch (JedisException je) {
            codis.close();
            throw je;
        }
        return new DefaultPooledObject<>(codis);
    }

    @Override
    public void passivateObject(PooledObject<Jedis> pooledCodis) throws Exception {
        // TODO maybe should select db 0? Not sure right now.
    }

    @Override
    public boolean validateObject(PooledObject<Jedis> pooledCodis) {
        final BinaryJedis jedis = pooledCodis.getObject();
        String addr = null;
        try {
            addr = jedis.getClient().getHost() + ":" + jedis.getClient().getPort();
            return codisAddrs.contains(addr) && jedis.isConnected() && jedis.ping().equals("PONG");
        } catch (final Exception e) {
            LOG.warn("Validate exception. addr:" + addr, e);
            return false;
        }
    }

    public void setCodisConnectionTimeoutMs(int codisConnectionTimeoutMs) {
        this.codisConnectionTimeoutMs = codisConnectionTimeoutMs;
    }

    public void setCodisPassword(String codisPassword) {
        this.codisPassword = codisPassword;
    }

    public void setCodisTimeoutMs(int codisTimeoutMs) {
        this.codisTimeoutMs = codisTimeoutMs;
    }

    public void setPoolConfig(JedisPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
    }

    public void setZkCodisProxyDir(String zkCodisProxyDir) {
        this.zkCodisProxyDir = zkCodisProxyDir;
    }

    public void setZkRetryPolicy(RetryPolicy zkRetryPolicy) {
        this.zkRetryPolicy = zkRetryPolicy;
    }

    public void setZkServers(String zkServers) {
        this.zkServers = zkServers;
    }

    public void setZkSessionTimeoutMs(int zkSessionTimeoutMs) {
        this.zkSessionTimeoutMs = zkSessionTimeoutMs;
    }
}
