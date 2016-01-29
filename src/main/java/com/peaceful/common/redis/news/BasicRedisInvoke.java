package com.peaceful.common.redis.news;

import com.peaceful.common.redis.proxy.JedisPoolService;
import com.peaceful.common.redis.proxy.JedisPoolServiceImpl;
import com.peaceful.common.redis.share.ShardJedisPoolService;
import com.peaceful.common.util.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCommands;
import redis.clients.util.Pool;

import java.lang.reflect.Method;
import java.util.concurrent.*;

/**
 * Created by wangjun on 16/1/29.
 */
public  class BasicRedisInvoke implements RedisInvoke {

    Logger logger = LoggerFactory.getLogger(getClass());
    ShardJedisPoolService shardJedisPoolService = ShardJedisPoolService.getShardJedisPoolService();
    JedisPoolService jedisPoolService = JedisPoolServiceImpl.getJedisPoolService();



    @Override
    public Object doInvoke(Method method, Object[] args, int type, String node) throws TimeoutException {
        if (method.getDeclaredAnnotations().equals(Object.class)) {
            return null;
        }

        Pool pool;
        if (type == RedisClientType.PROXY) {
            pool = jedisPoolService.getJedisPoolByHostName(node);
        } else {
            pool = shardJedisPoolService.getShardJedisPoolByClusterName(node);
        }
        if (pool == null) {
            throw new RuntimeException(String.format("Error: can't get redis connection ,because of not connection pool of %s", node));
        }

        Object result = null;
        JedisCommands commands = (JedisCommands) pool.getResource();
        if (commands == null) {
            throw new RuntimeException("Error: can't get available connection from pool");
        }
        try {
            return method.invoke(commands, args);
        } catch (Exception e) {
            pool.returnBrokenResource(commands);
            logger.error("Error:{}", ExceptionUtils.getStackTrace(e));
        } finally {
            pool.returnResource(commands);
        }

        return result;
    }


}
