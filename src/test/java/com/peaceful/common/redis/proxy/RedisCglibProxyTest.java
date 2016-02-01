package com.peaceful.common.redis.proxy;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCommands;

/**
 * Created by wangjun on 16/1/28.
 */
public class RedisCglibProxyTest {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testGetProxyInstance() throws Exception {
        RedisCglibProxy redisCglibProxy = new RedisCglibProxy(new RedisFutureInvoke());
        JedisCommands jedisCommands = redisCglibProxy.getProxyInstance(RedisClientType.PROXY, "haproxy");
        for (int i=0;i<100000;i++) {
            logger.info(jedisCommands.set("foo", "bar"));
            logger.info(jedisCommands.get("foo"));
        }
    }
}