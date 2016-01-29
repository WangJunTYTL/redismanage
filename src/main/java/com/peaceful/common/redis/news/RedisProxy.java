package com.peaceful.common.redis.news;

import redis.clients.jedis.JedisCommands;

/**
 * Created by wangjun on 16/1/28.
 */
public interface RedisProxy {

    JedisCommands getProxyInstance(int type, String node);


}
