package com.peaceful.common.redis.cglib;

/**
 * Date 14/11/3.
 * Author WangJun
 * Email wangjuntytl@163.com
 */
public interface UsageTracking {

    void costTime(String cmd, Object[] params, Object res, long time);

}
