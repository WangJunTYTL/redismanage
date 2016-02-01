package com.peaceful.common.redis.proxy;

/**
 * redis服务搭建类型
 *
 * @author WangJun <wangjuntytl@163.com>
 * @version 1.0 15/6/22
 * @since 1.6
 */

public interface RedisClientType {
    int PROXY = 1;//通过haproxy和Twitter提供的集群方式
    int SHARD = 2;//通过shardJedis提供的集群
}
