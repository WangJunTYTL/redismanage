package com.peaceful.common.redis.service;

import com.peaceful.common.redis.proxy.RedisCglibProxy;
import com.peaceful.common.redis.proxy.RedisClientType;
import com.peaceful.common.redis.proxy.RedisFutureInvoke;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCommands;

import java.util.HashMap;
import java.util.Map;

/**
 * 通过代理模式实现redis客户端组件，用于管理redis集群，通过给定的集群名称，便可获取一个可用的客户端，
 * 请注意该客户单并不是一个已经建立连接的redis客户端，只有真正执行命令时才会建立连接
 * <p/>
 * Created by wangjun on 15/2/10.
 */
public abstract class Redis {


    // 默认使用cglib实现代理，也可以使用jdk proxy实现，可参照 RedisJdkProxy
    private static RedisCglibProxy redisCglibProxy = new RedisCglibProxy(new RedisFutureInvoke());

    private static Map<String, JedisCommands> proxyContainer = new HashMap<String, JedisCommands>();
    private static Map<String, JedisCommands> shardContainer = new HashMap<String, JedisCommands>();


    private static Logger logger = LoggerFactory.getLogger(Redis.class);


    /**
     * @return 获取类型为proxy、集群名称为haproxy集群服务
     */
    public static JedisCommands cmd() {
        return cmd("haproxy");
    }

    /**
     * @param node 集群节点名称
     * @return 获取类型为proxy的指定集群节点服务
     */
    public static JedisCommands cmd(String node) {
        return getProxy(node, RedisClientType.PROXY);
    }

    /**
     * @return 获取默认集群服务节点服务
     */
    public static JedisCommands shardCmd() {
        return shardCmd("cacheCluster");
    }

    /**
     * @param node 集群节点名称
     * @return 获取类型为shard，指定集群名称的服务
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
            logger.info("load proxy for {} type {} Ok... ", node, type == RedisClientType.PROXY ? "PROXY" : "SHARD");
            return commands;
        }

    }


}
