package com.peaceful.common.redis.cglib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Date 14/11/3.
 * Author WangJun
 * Email wangjuntytl@163.com
 */
public class UsageTrackingImp implements UsageTracking {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public void costTime(String cmd, Object[] params, Object res, long time) {
        if (time > 10)
            logger.info("redis cmd is {} params is  {} result is {} cost time is {}", cmd, params, res, time);
    }
}
