package com.peaceful.common.redis.service;

import com.peaceful.common.redis.news.RedisCglibProxy;
import com.peaceful.common.redis.news.RedisClientType;
import com.peaceful.common.redis.news.RedisFutureInvoke;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCommands;

import java.util.HashMap;
import java.util.Map;

/**
 * redis 组件
 * Created by wangjun on 15/2/10.
 */
public abstract class Redis {


    private static RedisCglibProxy redisCglibProxy = new RedisCglibProxy(new RedisFutureInvoke());

    private static Map<String, JedisCommands> proxyContainer = new HashMap<String, JedisCommands>();
    private static Map<String, JedisCommands> shardContainer = new HashMap<String, JedisCommands>();


    static Logger logger = LoggerFactory.getLogger(Redis.class);


    /**
     * 获取通过haproxy集群redis服务
     *
     * @return
     */
    public static JedisCommands cmd() {
        return cmd("haproxy");
    }

    /**
     * 获取指定redis节点服务
     *
     * @param node
     * @return
     */
    public static JedisCommands cmd(String node) {
        return getProxy(node, RedisClientType.PROXY);
    }

    /**
     * 获取默认集群服务节点服务
     *
     * @return
     */
    public static JedisCommands shardCmd() {
        return shardCmd("cacheCluster");
    }

    /**
     * 获取指定集群服务节点服务
     *
     * @param node
     * @return
     */
    public static JedisCommands shardCmd(String node) {
        return getProxy(node, RedisClientType.SHARD);
    }

    private static JedisCommands getProxy(String node, int type) {

        Map<String, JedisCommands> container = null;
        if (type == RedisClientType.PROXY) {
            container = proxyContainer;
        } else if (type == RedisClientType.SHARD) {
            container = shardContainer;
        }
        if (container == null) {
            throw new RuntimeException("Error: type param is wrong !");
        }

        if (container.containsKey(node)) {
            return container.get(node);
        } else {
            JedisCommands commands = redisCglibProxy.getProxyInstance(type, node);
            container.put(node, commands);
            logger.info("load proxy for {} type {} Ok... ", node,type == RedisClientType.PROXY? "PROXY":"SHARD");
            return commands;
        }

    }


}
