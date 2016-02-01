package com.peaceful.common.redis.config;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by wangjun on 16/2/1.
 */
public class RedisConfigTest {

    @Test
    public void testGetPoolConfig() throws Exception {
        PoolConfig poolConfig = RedisConfig.create().getPoolConfig();
    }

    @Test
    public void testGetProxyClusterConfig() throws Exception {

    }

    @Test
    public void testGetShardClusterConfig() throws Exception {

    }
}