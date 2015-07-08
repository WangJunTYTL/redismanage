package com.peaceful.common.redis.proxy;

import com.peaceful.common.redis.config.AppConfigs;
import com.peaceful.common.redis.config.AppConfigsImpl;
import com.peaceful.common.redis.config.Application;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wangjun on 15/2/6.
 */
public class JedisPoolServiceImpl implements JedisPoolService {

    private static final Logger logger = LoggerFactory.getLogger(JedisPoolServiceImpl.class);
    private static final AppConfigs redisPoolConfig = AppConfigsImpl.getMyAppConfigs("redis/jedispool.properties");
    //最大分配的对象数
    public static final int redis_pool_maxActive = redisPoolConfig.getInt("redis.pool.maxActive");
    //最大能够保持idel状态的对象数
    public static final int redis_pool_maxIdle = redisPoolConfig.getInt("redis.pool.maxIdle");
    //当池内没有返回对象时，最大等待时间
    public static final int redis_pool_maxWait = redisPoolConfig.getInt("redis.pool.maxWait");
    //当调用borrow Object方法时，是否进行有效性检查
    public static final boolean redis_pool_testOnBorrow = redisPoolConfig.getBoolean("redis.pool.testOnBorrow");
    //当调用return Object方法时，是否进行有效性检查
    public static final boolean redis_pool_testOnReturn = redisPoolConfig.getBoolean("redis.pool.testOnReturn");

    RedisNodeService redisNodeService = RedisNodeServiceImpl.getRedisNodeService();

    private static Map<String, JedisPool> jedisPoolMap = new ConcurrentHashMap<String, JedisPool>();

    private JedisPoolServiceImpl() {
    }

    private static class SingletonHolder {
        private static JedisPoolService jedisPoolService = new JedisPoolServiceImpl();
    }

    public static JedisPoolService getJedisPoolService() {
        return SingletonHolder.jedisPoolService;
    }

    @Override
    public JedisPool getJedisPoolByHostName(String hostName) {
        if (jedisPoolMap.containsKey(hostName))
            return jedisPoolMap.get(hostName);
        RedisNode redisNode = redisNodeService.getRedisNode(hostName);
        String redis_ip = redisNode.getIp();
        int redis_port = redisNode.getPort();
        JedisPool pool = null;
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(redis_pool_maxActive);
        //允许空闲状态的个数
        config.setMaxIdle(redis_pool_maxIdle);
        //当需要一个redis连接时最长等待时间，否则抛出直接抛出JedisConnectionException
        config.setMaxWaitMillis(redis_pool_maxWait);
        //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的
        config.setTestOnBorrow(redis_pool_testOnBorrow);
        config.setTestOnReturn(redis_pool_testOnReturn);
        try {
            if (Application.isProduct() && StringUtils.isNotEmpty(redisNode.getPassward()))
                pool = new JedisPool(config, redis_ip, redis_port, redis_pool_maxWait, redisNode.getPassward());
            else pool = new JedisPool(config, redis_ip, redis_port, redis_pool_maxWait);

            jedisPoolMap.put(hostName, pool);
        } catch (Exception e) {
            logger.error("getPool:{}", e);
        }
        return pool;
    }
}
