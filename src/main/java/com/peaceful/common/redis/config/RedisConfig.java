package com.peaceful.common.redis.config;

import com.google.common.base.Throwables;
import com.peaceful.common.util.ExceptionUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import org.apache.commons.lang3.StringUtils;
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
                logger.error(Throwables.getStackTraceAsString(e));
                Throwables.propagate(e);
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
        Config con = config.getConfig("redis.proxy");
        Set<Map.Entry<String, ConfigValue>> entries = con.entrySet();
        for (Map.Entry<String, ConfigValue> value:entries){
            RedisNode redisNode = new RedisNode();
            redisNode.name = value.getKey();
            String meta = value.getValue().unwrapped().toString();
            if (StringUtils.isEmpty(meta)){
                throw new RuntimeException("redis node: "+redisNode.name+" is null");
            }
            String[] arr = meta.split(":");
            if (arr.length == 2){
                redisNode.ip = arr[0];
                redisNode.port = Integer.valueOf(arr[1]);
            }else if (arr.length == 3){
                redisNode.ip = arr[0];
                redisNode.port = Integer.valueOf(arr[1]);
                redisNode.passWard = arr[2];
            }else {
                throw new RuntimeException("redis node: "+redisNode.name+"  is a illegal config");
            }
            if (redisNodeMap.containsKey(redisNode.name)){
                throw new RuntimeException("redis node: "+redisNode.name+" has multi config");
            }
            redisNodeMap.put(redisNode.name, redisNode);
        }
    }

    public ShardClusterConfig getShardClusterConfig() {
        return shardClusterConfig;
    }

    private void setShardClusterConfig(Config config) {
        shardClusterConfig = new ShardClusterConfig();
        Config con = config.getConfig("redis.shard");
        Set<Map.Entry<String, ConfigValue>> entries = con.entrySet();
        for (Map.Entry<String, ConfigValue> value:entries){
            List<RedisNode> redisNodes = new ArrayList<RedisNode>();
            List<String> list  = (List<String>) value.getValue().unwrapped();
            for (String meta:list){
                if (StringUtils.isEmpty(meta)){
                    continue;
                }
                String[] arr = meta.split(":");
                RedisNode redisNode = new RedisNode();
                if (arr.length == 2){
                    redisNode.name=arr[0];
                    redisNode.ip = arr[0];
                    redisNode.port = Integer.valueOf(arr[1]);
                }else if (arr.length == 3){
                    redisNode.name = arr[0];
                    redisNode.ip = arr[0];
                    redisNode.port = Integer.valueOf(arr[1]);
                    redisNode.passWard = arr[2];
                }else {
                    throw new RuntimeException("redis node: "+redisNode.name+"  is a illegal config");
                }
                redisNodes.add(redisNode);
            }
            if (shardClusterConfig.shardClusterMap.containsKey(value.getKey())){
                throw new RuntimeException("redis shard node: "+value.getKey()+" has multi config");
            }
            shardClusterConfig.shardClusterMap.put(value.getKey(), redisNodes);
        }
    }
}
