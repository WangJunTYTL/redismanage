package com.peaceful.common.redis.proxy;

/**
 * Created by wangjun on 15/2/6.
 */
public class RedisConfigInitException extends RuntimeException {

    public RedisConfigInitException(String message, Throwable e){
        super(message,e);
    }
}
