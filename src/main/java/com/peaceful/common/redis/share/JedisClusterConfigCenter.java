/**
 *
 */
package com.peaceful.common.redis.share;

import com.peaceful.common.redis.proxy.RedisNode;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 集群配置中心
 * <p/>
 * Created by wangjun on 15/1/22.
 */
public class JedisClusterConfigCenter {

    static Logger logger = LoggerFactory.getLogger(JedisClusterConfigCenter.class);
    private static final ConcurrentMap<String, JedisClusterNode> clusterMap = new ConcurrentHashMap<String, JedisClusterNode>();

    //app启动时需要读取的集群配置文件
    static {
        loadConf();
    }

    private JedisClusterConfigCenter() {
    }


    public static JedisClusterNode getClusterConfig(String name) {
        return clusterMap.get(name);
    }

    public static void loadConf() {
        Config config = ConfigFactory.load("redis/redis-shard");
        ConfigObject configObject= config.getObject("redis");
        Set<String> keys = configObject.keySet();
        for(String key:keys){
            ConfigObject clusterNode = (ConfigObject) configObject.get(key);
            Set<String> nodes = clusterNode.keySet();
            List<RedisNode> redisNodes = new CopyOnWriteArrayList<RedisNode>();
            for(String hostName:nodes){
                RedisNode redisNode = new RedisNode();
                ConfigObject node = (ConfigObject) clusterNode.get(hostName);
                Config nodeConfig = node.toConfig();
                redisNode.setHostName(hostName);
                redisNode.setIp(nodeConfig.getString("ip"));
                redisNode.setPort(nodeConfig.getInt("port"));
                if (nodeConfig.hasPath("password")) redisNode.setPassward(nodeConfig.getString("password"));
                redisNodes.add(redisNode);
            }
            JedisClusterNode jedisClusterNode = new JedisClusterNode();
            jedisClusterNode.setClusterName(key);
            jedisClusterNode.setRedisNodeList(redisNodes);
            clusterMap.put(key,jedisClusterNode);
        }

    }
}
