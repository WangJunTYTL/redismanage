package com.peaceful.common.redis.proxy;

import redis.clients.jedis.JedisCommands;

/**
 * 利用代理模式实现redis服务的管理、调用，
 * <p/>
 * 用户只需要输入需要连接的集群服务名称，便可返回一个Jedis的客户端，该客户端并不代表一个可用的连接，
 * 请注意：只有当用户真正执行一个命令时才会从连接池中获取连接，执行，释放连接 返回结果。
 * <p/>
 * Created by wangjun on 16/1/28.
 */
public interface RedisProxy {

    /**
     * @param type 搭建的redis服务方式，分为两种：Shard 与 proxy
     * @param node 所用集群节点
     * @return 返回一个待使用的jedis客户端，并不是一个已经建立连接的jedis对象
     */
    JedisCommands getProxyInstance(int type, String node);


}
