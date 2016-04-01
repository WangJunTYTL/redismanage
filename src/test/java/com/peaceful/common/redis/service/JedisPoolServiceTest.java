package com.peaceful.common.redis.service;

import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static org.junit.Assert.*;

/**
 * @author WangJun
 * @version 1.0 16/4/1
 */
public class JedisPoolServiceTest {

    @Test
    public void getJedisPoolService() throws Exception {
        JedisPool pool = JedisPoolService.getJedisPoolService().getJedisPoolByHostName("haproxy");
        Jedis jedis = pool.getResource();
        jedis.hdel("aa",null);

    }
}