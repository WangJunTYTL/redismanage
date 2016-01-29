package com.peaceful.common.redis.proxy;

import com.peaceful.common.redis.config.AppConfigs;
import com.peaceful.common.redis.config.AppConfigsImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wangjun on 15/2/6.
 */
public class RedisNodeServiceImpl implements RedisNodeService {

    private static final Logger logger = LoggerFactory.getLogger(RedisNodeServiceImpl.class);
    private static AppConfigs appConfigs = AppConfigsImpl.getMyAppConfigs("redis/redisnodes.properties");
    private static Map<String, RedisNode> redisNodeMap = new ConcurrentHashMap<String, RedisNode>();
    static final int UNINITIALIZED = 0;
    static final int SUCCESSFUL_INITIALIZATION = 1;
    static int INITIALIZATION_STATE = UNINITIALIZED;


    private RedisNodeServiceImpl() {
        logger.info("----------------------------------------------");
        logger.info("load redis proxy config Ok...");
        logger.info("==============================================");
        for (RedisNode node:redisNodeMap.values()){
            logger.info("Node: {}  {}:{}",node.getHostName(),node.getIp(),node.getPort());
        }
        logger.info("----------------------------------------------");
    }

    private static class SingletonHolder {
        private static RedisNodeService redisNodeService = new RedisNodeServiceImpl();
    }

    public static RedisNodeService getRedisNodeService() {
        if (INITIALIZATION_STATE == SUCCESSFUL_INITIALIZATION)
            return SingletonHolder.redisNodeService;
        try {
            Map<String, String> kv = appConfigs.toMap();
            Set<String> names = kv.keySet();
            for (String name : names) {
                name = name.substring(0,name.indexOf("."));
                if(redisNodeMap.containsKey(name))
                    continue;
                RedisNode redisNode = new RedisNode();
                redisNode.setHostName(name);
                redisNode.setIp(appConfigs.getString(name.concat(".ip")));
                redisNode.setPort(appConfigs.getInt(name.concat(".port")));
                if (StringUtils.isNotEmpty(appConfigs.getString(name.concat(".password"))))
                    redisNode.setPassward(appConfigs.getString(name.concat(".password")));
                redisNodeMap.put(name, redisNode);
            }
            INITIALIZATION_STATE = SUCCESSFUL_INITIALIZATION;
        } catch (Exception e) {
            INITIALIZATION_STATE = UNINITIALIZED;
            throw new RedisConfigInitException("repo init config error ,please review you config ", e);
        }
        return SingletonHolder.redisNodeService;
    }


    @Override
    public RedisNode getRedisNode(String name) {
        return redisNodeMap.get(name);
    }

}
