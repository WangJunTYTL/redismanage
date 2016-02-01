package com.peaceful.common.redis.proxy;

import java.lang.reflect.Method;
import java.util.concurrent.TimeoutException;

/**
 * Redis命令执行接口
 * <p/>
 * Created by wangjun on 16/1/29.
 */
public interface RedisInvoke {


    /**
     * 只需要知道redis命令的方法与参数，获取指定集群的可用redis连接进行命令调用
     *
     * @param method 执行命令
     * @param args   参数
     * @param type   集群类型
     * @param node   集群节点
     * @return 命令执行结果
     * @throws TimeoutException 如果在设定时间内没有返回结果并释放连接则抛出该异常
     */
    Object doInvoke(Method method, Object[] args, int type, String node) throws TimeoutException;


}
