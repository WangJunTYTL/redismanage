package com.peaceful.common.redis.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangjun on 16/2/1.
 */
public class ShardClusterConfig {
   public Map<String, List<RedisNode>> shardClusterMap = new HashMap<String, List<RedisNode>>();
}
