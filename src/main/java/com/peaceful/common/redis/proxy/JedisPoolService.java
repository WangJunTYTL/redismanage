package com.peaceful.common.redis.proxy;

import redis.clients.jedis.JedisPool;

/**
 * Created by wangjun on 15/2/6.
 */
public interface JedisPoolService {

    /**
     * 获取指定hostname的连接池
     *
     * @param hostName
     * @return
     */
    JedisPool getJedisPoolByHostName(String hostName);


}
