package com.peaceful.common.redis.news;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCommands;

/**
 * Created by wangjun on 16/1/29.
 */
public class RedisJdkProxyTest {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testGetProxyInstance() throws Exception {
        RedisProxy redisProxy = new RedisJdkProxy(new RedisFutureInvoke());
        JedisCommands jedisCommands = redisProxy.getProxyInstance(RedisClientType.PROXY,"haproxy");
        logger.info(jedisCommands.get("foo"));
    }
}