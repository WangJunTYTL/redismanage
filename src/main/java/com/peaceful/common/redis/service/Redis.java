package com.peaceful.common.redis.service;

import com.peaceful.common.redis.RedisType;
import com.peaceful.common.redis.cglib.CglibProxySource;
import com.peaceful.common.redis.cglib.ProxySource;
import com.peaceful.common.redis.cglib.UsageTrackingImp;
import redis.clients.jedis.JedisCommands;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * redis 组件
 * Created by wangjun on 15/2/10.
 */
public abstract class Redis {


    private static ProxySource<JedisCommands> proxySource;
    private static Map<String,JedisCommands> commandsMap = new ConcurrentHashMap<String, JedisCommands>();



    static {
        proxySource = new CglibProxySource<JedisCommands>(JedisCommands.class);
    }


    private static JedisCommands createProxy(String hostname){
        if (commandsMap.containsKey(hostname)){
            return commandsMap.get(hostname);
        }else {
            JedisCommands commands = proxySource.createProxy(hostname, new UsageTrackingImp(), RedisType.PROXY);
            commandsMap.put(hostname,commands);
            return commands;
        }
    }

    /**
     * 获取通过haproxy集群redis服务
     *
     * @return
     */
    public static JedisCommands cmd() {
       return proxySource.createProxy("haproxy", new UsageTrackingImp(), RedisType.PROXY);
//        return createProxy("haproxy");
    }

    /**
     * 获取指定redis节点服务
     *
     * @param hostName
     * @return
     */
    public static JedisCommands cmd(String hostName) {
        return proxySource.createProxy(hostName, new UsageTrackingImp(), RedisType.PROXY);
//        return createProxy(hostName);

    }

    /**
     * 获取默认集群服务节点服务
     *
     * @return
     */
    public static JedisCommands shardCmd() {
        return proxySource.createProxy("cacheCluster", new UsageTrackingImp(), RedisType.SHARD);
    }

    /**
     * 获取指定集群服务节点服务
     *
     * @param clusterName
     * @return
     */
    public static JedisCommands shardCmd(String clusterName) {
        return proxySource.createProxy(clusterName, new UsageTrackingImp(), RedisType.SHARD);
    }


}
