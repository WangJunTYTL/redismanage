package com.peaceful.common.redis.service;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.Util;

import static org.junit.Assert.*;

/**
 * @author wangjun
 * @since 15/7/8.
 */
public class RedisTest {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testCmd() throws Exception {
        for (int i = 0; i < 10; i++)
            logger.info(Redis.cmd().get("foo"));
    }

    @Test
    public void testShardCmd() throws Exception {
        logger.info(Redis.shardCmd("cacheCluster01").get("foo"));
    }

    @Test
    public void testException() throws Exception {
        Redis.cmd().hdel("aaa",null);
    }



}