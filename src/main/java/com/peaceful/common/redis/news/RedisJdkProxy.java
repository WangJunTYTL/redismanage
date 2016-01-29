package com.peaceful.common.redis.news;

import redis.clients.jedis.JedisCommands;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 利用JDK实现代理模式，获取用户执行的命令、参数、服务节点
 * <p/>
 * Created by wangjun on 16/1/28.
 */
public class RedisJdkProxy implements RedisProxy {


    private RedisInvoke redisInvoke;

    public RedisJdkProxy(RedisInvoke redisInvoke) {
        this.redisInvoke = redisInvoke;
    }


    @Override
    public JedisCommands getProxyInstance(final int type, final String node) {
        return (JedisCommands) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{JedisCommands.class}, new java.lang.reflect.InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getDeclaredAnnotations().equals(Object.class)) {
                    return null;
                }
                return redisInvoke.doInvoke(method, args, type, node);
            }
        });
    }
}
