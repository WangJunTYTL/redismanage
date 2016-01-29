package com.peaceful.common.redis.news;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;
import redis.clients.jedis.JedisCommands;

import java.lang.reflect.Method;

/**
 * Created by wangjun on 16/1/28.
 */
public class RedisCglibProxy implements RedisProxy {


    private RedisInvoke redisInvoke;

    public RedisCglibProxy(RedisInvoke redisInvoke) {
        this.redisInvoke = redisInvoke;
    }


    @Override
    public JedisCommands getProxyInstance(final int type, final String node) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(JedisCommands.class);
        enhancer.setCallback(new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                return redisInvoke.doInvoke(method, objects, type,node);
            }
        });
        JedisCommands t = (JedisCommands) enhancer.create();
        return t;
    }
}
