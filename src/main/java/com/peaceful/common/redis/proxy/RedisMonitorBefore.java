package com.peaceful.common.redis.proxy;

import com.peaceful.common.util.chain.Context;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 利用perf4j对redis命令调用前监控埋点
 * <p/>
 * Created by wangjun on 16/1/28.
 */
public class RedisMonitorBefore implements RedisInvokeAop {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean execute(Context invokeContext) throws Exception {
        invokeContext.put("redis.stopWatch", new Slf4JStopWatch("REDIS"));
        return false;
    }
}
