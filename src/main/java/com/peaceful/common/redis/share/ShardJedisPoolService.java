package com.peaceful.common.redis.share;

import com.peaceful.common.redis.config.Application;
import com.peaceful.common.redis.proxy.RedisNode;
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

    private static final int POOL_TOTAL_MAX_COUNT = 28;
    private static final int POOL_MAX_COUNT = 8;
    private static final Logger logger = LoggerFactory.getLogger(ShardJedisPoolService.class);

    public ShardedJedisPool getShardJedisPoolByClusterName(String clusterName) {
        if (clusterMap.containsKey(clusterName)) {
            return clusterMap.get(clusterName);
        }
        JedisClusterNode config = JedisClusterConfigCenter.getClusterConfig(clusterName);
        if (config == null) {
            throw new RuntimeException("JedisClusterConfigNode: " + clusterName + " is not exist");
        }
        ShardedJedisPool shardedJedisPool = loadShardJedisPool(config);
        clusterMap.put(clusterName, shardedJedisPool);
        return shardedJedisPool;
    }

    /**
     * 初始化shardjedispool
     *
     * @param config
     * @return
     */
    private ShardedJedisPool loadShardJedisPool(JedisClusterNode config) {
        try {
            //每个redis节点池配置
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setTestWhileIdle(true);
            //最大连接数, 默认28个
            poolConfig.setMaxTotal(POOL_TOTAL_MAX_COUNT);
            //最大空闲连接数, 默认8个
            poolConfig.setMaxIdle(POOL_MAX_COUNT);
            //连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
            poolConfig.setBlockWhenExhausted(true);
            //获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
            poolConfig.setMaxWaitMillis(3000);
            poolConfig.setTestOnBorrow(false);
            poolConfig.setTestOnReturn(false);
            List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
            for (RedisNode redisNode : config.getRedisNodeList()) {
                //也可以设置权重
                JedisShardInfo jsi = new JedisShardInfo(redisNode.getIp(), redisNode.getPort(), 0);
                if (Application.isProduct() && StringUtils.isNotEmpty(redisNode.getPassward())) {
                    jsi.setPassword(redisNode.getPassward());
                }
                shards.add(jsi);
            }
            //直连
//            ShardedJedis jedis = new ShardedJedis(shards);
            if (shards == null || shards.size() == 0)
                throw new RuntimeException("can't return a new  shardedJedisPool,shardInfo is empty");
            return new ShardedJedisPool(poolConfig, shards);
        } catch (Throwable e) {
            logger.error("init shardJedisPool error {} ", e);
            throw new RuntimeException(e);
        }
    }
}