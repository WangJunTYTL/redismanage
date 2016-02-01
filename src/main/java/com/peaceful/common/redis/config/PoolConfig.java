package com.peaceful.common.redis.config;

/**
 * jedis连接池配置
 * <p/>
 * Created by wangjun on 16/2/1.
 */
public class PoolConfig {

    /**
     * 最大连接数
     */
    public int maxActive;
    /**
     * 最小连接数
     */
    public int maxIdle;
    /**
     * 最大等待时间
     */
    public int maxWait;
    /**
     * 是否在使用连接时检验连接可用性
     */
    public boolean testOnBorrow;
    /**
     * 是否在释放连接时检验连接可用性
     */
    public boolean testOnReturn;


}
