package com.peaceful.common.redis.news;

import redis.clients.util.Pool;

import java.lang.reflect.Method;
import java.util.concurrent.TimeoutException;

/**
 * Redis命令执行接口
 * <p/>
 * Created by wangjun on 16/1/29.
 */
public interface RedisInvoke {


    /**
     * 只需要知道redis命令的方法与参数，并在invokeContext获取可用的redis连接资源便可进行调用
     *
     * @param method
     * @param args
     * @param invokeContext
     * @return
     */
    Object doInvoke(Method method, Object[] args, int type, String node) throws TimeoutException;


}
