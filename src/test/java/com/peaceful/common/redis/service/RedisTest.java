package com.peaceful.common.redis.service;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author wangjun
 * @since 15/7/8.
 */
public class RedisTest {

    @Test
    public void testCmd() throws Exception {
        Redis.cmd().get("foo");
    }

    @Test
    public void testCmd1() throws Exception {

    }

    @Test
    public void testShardCmd() throws Exception {

    }

    @Test
    public void testShardCmd1() throws Exception {

    }
}