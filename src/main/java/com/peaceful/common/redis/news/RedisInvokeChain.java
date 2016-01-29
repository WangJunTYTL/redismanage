package com.peaceful.common.redis.news;

import com.peaceful.common.util.chain.BaseChain;
import com.peaceful.common.util.chain.Chain;

/**
 * Created by wangjun on 16/1/28.
 */
public class RedisInvokeChain extends BaseChain {


    public static Chain invokeBefore = new BaseChain();

    public static Chain invokeAfter = new BaseChain();


    static {
        invokeBefore.addCommand(new RedisMonitorBefore());
        invokeAfter.addCommand(new RedisMonitorAfter());
    }


}
