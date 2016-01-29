package com.peaceful.common.redis.news;

import com.peaceful.common.util.chain.Context;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wangjun on 16/1/28.
 */
public class RedisMonitorBefore implements RedisInvokePlugin {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean execute(Context invokeContext) throws Exception {
        invokeContext.put("redis.stopWatch", new Slf4JStopWatch("REDIS"));
        return false;
    }
}
