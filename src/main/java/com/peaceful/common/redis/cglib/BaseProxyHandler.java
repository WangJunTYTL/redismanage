package com.peaceful.common.redis.cglib;

import com.peaceful.common.redis.RedisType;
import com.peaceful.common.redis.proxy.JedisPoolServiceImpl;
import com.peaceful.common.redis.proxy.RedisNodeServiceImpl;
import com.peaceful.common.redis.share.ShardJedisPoolService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.lang.reflect.Method;

/**
 * Date 14/11/3.
 * Author WangJun
 * Email wangjuntytl@163.com
 */
public class BaseProxyHandler<T> {

    private static Logger logger = LoggerFactory.getLogger("redis proxy");
    private String redisNode;
    private int redisType;
    private UsageTracking usageTracking;
    private Object jedisPool;
    private T redisClient;
    private boolean isShare;

    BaseProxyHandler(String redisNode, UsageTracking usageTracking, int redisType) {
        this.redisNode = redisNode;
        this.redisType = redisType;
        this.usageTracking = usageTracking;
    }


    /**
     * Obtain the wrapped, pooled object.
     *
     * @return the underlying pooled object
     */
    T getJedisObject() {
        try {
            if (redisType == RedisType.PROXY) {
                JedisPool jedisPool = JedisPoolServiceImpl.getJedisPoolService().getJedisPoolByHostName(redisNode);
                redisClient = (T) jedisPool.getResource();
                this.jedisPool = jedisPool;
            } else if (redisType == RedisType.SHARD) {
                ShardedJedisPool jedisPool = ShardJedisPoolService.getShardJedisPoolService().getShardJedisPoolByClusterName(redisNode);
                redisClient = (T) jedisPool.getResource();
                this.jedisPool = jedisPool;
                isShare = true;
            }
            return redisClient;
        } catch (Exception e) {
            logger.error("redis error {}", ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("can't get jedis client from pool,redis cluster is " + redisNode + ":" + RedisNodeServiceImpl.getRedisNodeService().getRedisNode(redisNode), e);
        }
    }


    /**
     * Disable the proxy wrapper. Called when the object has been returned to
     * the pool. Further use of the wrapper should result in an
     * {@link IllegalStateException}.
     *
     * @return the object that this proxy was wrapping
     */
    T disableProxy() {
        T result = redisClient;
        redisClient = null;
        jedisPool = null;
        usageTracking = null;
        this.redisNode = null;
        return result;
    }

    Object doInvoke(Method method, Object[] args) throws Throwable {
        if (StringUtils.isEmpty(redisNode)) {
            logger.debug("redis node is empty");
            return disableProxy();
        } else if (method.getDeclaringClass() == Object.class) {
            logger.debug("Object declaring method invoke {}", method.getName());
            return disableProxy();
        }
        long now = System.currentTimeMillis();
        Object r = null;
        boolean flag = true;
        try {
            getJedisObject();
            r = method.invoke(redisClient, args);
        } catch (Exception e) {
            flag = false;
            logger.error("redis error {}", ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("redis cmd exe error..please ensure your params is right . current cmd is " + method.getName() + " params is " + args.toString() + " error stack is " + ExceptionUtils.getStackTrace(e));

        } finally {
            if (flag) {
                if (isShare) {
                    ShardedJedisPool shardedJedisPool = (ShardedJedisPool) jedisPool;
                    if (redisClient != null)
                        shardedJedisPool.returnResource((ShardedJedis) redisClient);
                } else {
                    JedisPool jedisPool1 = (JedisPool) jedisPool;
                    if (redisClient != null)
                        jedisPool1.returnResource((Jedis) redisClient);
                }
            } else {
                if (isShare) {
                    ShardedJedisPool shardedJedisPool = (ShardedJedisPool) jedisPool;
                    shardedJedisPool.returnBrokenResource((ShardedJedis) redisClient);
                } else {
                    JedisPool jedisPool1 = (JedisPool) jedisPool;
                    jedisPool1.returnBrokenResource((Jedis) redisClient);
                }
            }
        }
        if (usageTracking != null) {
            if (method.getDeclaringClass() != Object.class)
                usageTracking.costTime(method.getName(), args, r, System.currentTimeMillis() - now);
            else
                logger.info("redis proxy method {}", method.getName());
        }
        disableProxy();
        return r;
    }
}
