package com.peaceful.common.redis.news;

import com.peaceful.common.util.chain.BaseChain;
import com.peaceful.common.util.chain.Chain;

/**
 * redis命令开始执行前和执行后调用链
 * <p/>
 * Created by wangjun on 16/1/28.
 */
public class RedisInvokeChain extends BaseChain {


    /**
     * 命令调用前执行链
     */
    public static Chain invokeBefore = new BaseChain();

    /**
     * 命令调用后执行链
     */
    public static Chain invokeAfter = new BaseChain();


    static {
        invokeBefore.addCommand(new RedisMonitorBefore());
        invokeAfter.addCommand(new RedisMonitorAfter());
    }


}
