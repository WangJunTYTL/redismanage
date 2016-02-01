package com.peaceful.common.redis.service;

import com.peaceful.common.redis.config.RedisConfig;
import com.peaceful.common.redis.config.RedisNode;
import com.peaceful.common.redis.config.ShardClusterConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 根据指定的节点获取节点的shardJedisPool
 * <p/>
 * Created by wangjun on 15/1/22.
 */
public class ShardJedisPoolService {


    private ShardJedisPoolService() {
    }

    private static Map<String, ShardedJedisPool> clusterMap = new ConcurrentHashMap<String, ShardedJedisPool>();

    private static class SingletonHolder {
        private static ShardJedisPoolService shardJedisPoolService = new ShardJedisPoolService();
    }

    public static ShardJedisPoolService getShardJedisPoolService() {
        return SingletonHolder.shardJedisPoolService;
    }

    private static final Logger logger = LoggerFactory.getLogger(ShardJedisPoolService.class);

    /**
     * 获取指定节点的shardJedisPool
     *
     * @param nodeName 节点名
     * @return ShardJedisPool
     */
    public ShardedJedisPool getShardJedisPoolByClusterName(String nodeName) {
        if (clusterMap.containsKey(nodeName)) {
            return clusterMap.get(nodeName);
        }
        ShardClusterConfig shardClusterConfig = RedisConfig.create().getShardClusterConfig();
        if (shardClusterConfig.shardClusterMap.containsKey(nodeName)) {
            List<RedisNode> redisNodeList = shardClusterConfig.shardClusterMap.get(nodeName);
            if (redisNodeList == null || redisNodeList.size() == 0) {
                throw new RuntimeException("Error:Note " + nodeName + " is empty");
            }
            ShardedJedisPool shardedJedisPool = loadShardJedisPool(redisNodeList);
            clusterMap.put(nodeName, shardedJedisPool);
            logger.info("load connections pool for {} Ok... ", nodeName);
            return shardedJedisPool;
        } else {
            throw new RuntimeException("Not Found Node  " + nodeName);
        }
    }

    /**
     * 初始化shardjedispool
     *
     * @param config
     * @return
     */
    private ShardedJedisPool loadShardJedisPool(List<RedisNode> config) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMaxTotal(RedisConfig.create().getPoolConfig().maxActive);
        poolConfig.setMaxIdle(RedisConfig.create().getPoolConfig().maxIdle);
        poolConfig.setMaxWaitMillis(RedisConfig.create().getPoolConfig().maxWait);
        poolConfig.setTestOnBorrow(RedisConfig.create().getPoolConfig().testOnBorrow);
        poolConfig.setTestOnReturn(RedisConfig.create().getPoolConfig().testOnReturn);
        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
        for (RedisNode redisNode : config) {
            JedisShardInfo jsi = new JedisShardInfo(redisNode.ip, redisNode.port, 0);
            if (StringUtils.isNotEmpty(redisNode.passWard)) {
                jsi.setPassword(redisNode.passWard);
            }
            shards.add(jsi);
        }
        return new ShardedJedisPool(poolConfig, shards);
    }
}