package com.peaceful.common.redis.cglib;

/**
 * Date 14/11/3.
 * Author WangJun
 * Email wangjuntytl@163.com
 */
public interface ProxySource<T> {


    T createProxy(String redisNode, UsageTracking usageTracking, int redisType);


    T resolveProxy(T proxy);
}
