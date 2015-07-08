package com.peaceful.common.redis.cglib;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Date 14/11/3.
 * Author WangJun
 * Email wangjuntytl@163.com
 */
public class CglibProxyHandler<T> extends BaseProxyHandler<T>
        implements MethodInterceptor {


    CglibProxyHandler(String redisNode, UsageTracking usageTracking, int redisType) {
        super(redisNode, usageTracking, redisType);
    }

    @Override
    public Object intercept(Object object, Method method, Object[] args,
                            MethodProxy methodProxy) throws Throwable {
        return doInvoke(method, args);
    }
}
