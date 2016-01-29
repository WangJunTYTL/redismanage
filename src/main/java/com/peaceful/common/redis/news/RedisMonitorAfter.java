package com.peaceful.common.redis.news;

import com.peaceful.common.util.chain.Context;
import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 利用perf4j对redis执行后监控埋点处理：统计调用次数TPS，响应时间Mean，以及慢执行命令
 * <p/>
 * Created by wangjun on 16/1/28.
 */
public class RedisMonitorAfter implements RedisInvokeAop {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean execute(Context invokeContext) throws Exception {
        StopWatch stopWatch = (StopWatch) invokeContext.get("redis.stopWatch");
        long cost = stopWatch.getElapsedTime();
        String node = (String) invokeContext.get("redis.node");
        stopWatch.stop("REDIS." + node);
        stopWatch.stop("REDIS");
        if (cost > 500) {
            logger.warn("slow cmd: {}{} cost {} ms at {} node , result {}", invokeContext.get("redis.cmd"), invokeContext.get("redis.args"), cost, invokeContext.get("redis.node"), invokeContext.get("result"));
        } else {
            logger.debug("cmd : {}{} cost {} ms at {} node", invokeContext.get("redis.cmd"), invokeContext.get("redis.args"), cost, invokeContext.get("redis.node"));
        }
        return false;
    }
}
