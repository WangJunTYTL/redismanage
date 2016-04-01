package com.peaceful.common.redis.config;

import com.peaceful.common.util.ExceptionUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangjun on 16/2/1.
 */
public class RedisConfig {

    private static int STATE = 0;

    private PoolConfig poolConfig;
    private ProxyClusterConfig proxyClusterConfig;
    private ShardClusterConfig shardClusterConfig;
    public long futureInvokeTimeout = 2000;
    private static Logger logger = LoggerFactory.getLogger(RedisConfig.class);


    private RedisConfig() {
    }

    public synchronized static RedisConfig create() {
        if (STATE == 0) {
            try {
                Config config = ConfigFactory.load("redis/redis.conf");
                Single.redisConfig.setPoolConfig(config);
                Single.redisConfig.setProxyClusterConfig(config);
                Single.redisConfig.setShardClusterConfig(config);
                Single.redisConfig.futureInvokeTimeout = config.getDuration("redis.future.invoke.timeout", TimeUnit.MILLISECONDS);
                logger.info("=====================================");
                logger.info("redis config");
                logger.info("-------------------------------------");
                logger.info("maxActive: {}", Single.redisConfig.getPoolConfig().maxActive);
                logger.info("maxIdle: {}", Single.redisConfig.getPoolConfig().maxIdle);
                logger.info("maxWait: {}ms", Single.redisConfig.getPoolConfig().maxWait);
                logger.info("testOnBorrow: {}", Single.redisConfig.getPoolConfig().testOnBorrow);
                logger.info("testOnReturn: {}", Single.redisConfig.getPoolConfig().testOnReturn);
                logger.info("proxy.cluster.count: {}", Single.redisConfig.getProxyClusterConfig().redisNodeMap.size());
                logger.info("shard.cluster.count: {}", Single.redisConfig.getShardClusterConfig().shardClusterMap.size());
                logger.info("future.invoke.timeout: {}ms", Single.redisConfig.futureInvokeTimeout);
                logger.info("=====================================");
            } catch (Exception e) {
                STATE = -1;
                throw new RuntimeException(ExceptionUtils.getStackTrace(e));
            }
            STATE = 1;
            return Single.redisConfig;
        } else if (STATE == 1) {
            return Single.redisConfig;
        } else {
            throw new RuntimeException("Error: can't load redis config");
        }
    }

    private static class Single {
        static RedisConfig redisConfig = new RedisConfig();
    }

    public PoolConfig getPoolConfig() {
        return poolConfig;
    }

    private void setPoolConfig(Config config) {
        poolConfig = new PoolConfig();
        poolConfig.maxActive = config.getInt("redis.pool.maxActive");
        poolConfig.maxIdle = config.getInt("redis.pool.maxIdle");
        poolConfig.maxWait = config.getInt("redis.pool.maxWait");
        poolConfig.testOnBorrow = config.getBoolean("redis.pool.testOnBorrow");
        poolConfig.testOnReturn = config.getBoolean("redis.pool.testOnReturn");
    }

    public ProxyClusterConfig getProxyClusterConfig() {
        return proxyClusterConfig;
    }

    private void setProxyClusterConfig(Config config) {
        proxyClusterConfig = new ProxyClusterConfig();
        Map<String, RedisNode> redisNodeMap = proxyClusterConfig.redisNodeMap;
        List<? extends ConfigObject> executors = config.getObjectList("redis.proxy");
        for (ConfigObject object : executors) {
            RedisNode redisNode = new RedisNode();
            redisNode.name = object.get("name").unwrapped().toString();
            redisNode.ip = object.get("ip").unwrapped().toString();
            if (object.containsKey("password"))
                redisNode.passWard = object.get("password").unwrapped().toString();
            redisNode.port = Integer.valueOf(object.get("port").unwrapped().toString());
            if (redisNodeMap.containsKey(redisNode.name)) {
                throw new RuntimeException("Error: There are two " + redisNode.name);
            } else {
                redisNodeMap.put(redisNode.name, redisNode);
            }
        }
    }

    public ShardClusterConfig getShardClusterConfig() {
        return shardClusterConfig;
    }

    private void setShardClusterConfig(Config config) {
        shardClusterConfig = new ShardClusterConfig();
        ConfigObject clusterMap = (ConfigObject) config.getObject("redis.shard");
        Set<String> keys = clusterMap.keySet();
        for (String key : keys) {
            ConfigObject clusterNode = (ConfigObject) clusterMap.get(key);
            Set<String> nodes = clusterNode.keySet();
            List<RedisNode> redisNodes = new ArrayList<RedisNode>();
            for (String hostName : nodes) {
                RedisNode redisNode = new RedisNode();
                ConfigObject node = (ConfigObject) clusterNode.get(hostName);
                Config nodeConfig = node.toConfig();
                redisNode.name = hostName;
                redisNode.ip = nodeConfig.getString("ip");
                redisNode.port = nodeConfig.getInt("port");
                if (nodeConfig.hasPath("password")) redisNode.passWard = (nodeConfig.getString("password"));
                redisNodes.add(redisNode);
            }
            shardClusterConfig.shardClusterMap.put(key, redisNodes);
        }
    }
}
