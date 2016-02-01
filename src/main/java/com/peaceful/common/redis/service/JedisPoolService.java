package com.peaceful.common.redis.service;

import com.peaceful.common.redis.config.RedisConfig;
import com.peaceful.common.redis.config.RedisNode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 根据指定的节点获取节点的jedispool
 * <p/>
 * Created by wangjun on 15/2/6.
 */
public class JedisPoolService {

    private static final Logger logger = LoggerFactory.getLogger(JedisPoolService.class);

    private static Map<String, JedisPool> jedisPoolMap = new HashMap<String, JedisPool>();

    private JedisPoolService() {
    }

    private static class SingletonHolder {
        private static JedisPoolService jedisPoolService = new JedisPoolService();
    }

    public static JedisPoolService getJedisPoolService() {
        return SingletonHolder.jedisPoolService;
    }

    /**
     * 获取指定节点的jedispool
     *
     * @param nodeName 节点名
     * @return jedispool
     */
    public JedisPool getJedisPoolByHostName(String nodeName) {
        if (jedisPoolMap.containsKey(nodeName))
            return jedisPoolMap.get(nodeName);
        RedisNode redisNode = RedisConfig.create().getProxyClusterConfig().redisNodeMap.get(nodeName);
        String ip = redisNode.ip;
        int port = redisNode.port;
        JedisPool pool;
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(RedisConfig.create().getPoolConfig().maxActive);
        config.setMaxIdle(RedisConfig.create().getPoolConfig().maxIdle);
        config.setMaxWaitMillis(RedisConfig.create().getPoolConfig().maxWait);
        config.setTestOnBorrow(RedisConfig.create().getPoolConfig().testOnBorrow);
        config.setTestOnReturn(RedisConfig.create().getPoolConfig().testOnReturn);
        if (StringUtils.isNotEmpty(redisNode.passWard)) {
            pool = new JedisPool(config, ip, port, 2000, redisNode.passWard);
        } else {
            pool = new JedisPool(config, ip, port, 2000);
        }
        logger.info("load connections pool for {} Ok... ", nodeName);
        jedisPoolMap.put(nodeName, pool);
        return pool;
    }
}
