/**
 *
 */
package com.peaceful.common.redis.share;


import com.peaceful.common.redis.proxy.RedisNode;

import java.util.List;

/**
 * jedis cluster config
 * <p/>
 * Created by wangjun on 15/1/22.
 */
public class JedisClusterNode {

    private String clusterName;

    private List<RedisNode> redisNodeList;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public List<RedisNode> getRedisNodeList() {
        return redisNodeList;
    }

    public void setRedisNodeList(List<RedisNode> redisNodeList) {
        this.redisNodeList = redisNodeList;
    }
}
