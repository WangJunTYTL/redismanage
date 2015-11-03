# redis manage

统一管理redis的集群服务，方便各个业务申请专用的redis服务集群节点。

比如有业务部门A和B,A和B使用不同的redis集群服务，你只需要配置

    A.ip=127.0.0.1
    A.port=6379

    B.ip=127.0.0.1
    B.port=6379

A业务部门使用方式

    Redis.cmd(A).get("foo")
    
B业务部门使用：
    
    Redis.cmd(B).get("foo")


## 支持集群方式：

### jedis ShardJedis
 
在java jedis组件中提供了一种集群方式：ShardJedis。它会对用户使用到的key进行hash计算，然后确定是利用集群中的某个节点。如果在你的项目中集群方式是这样的
可以利用的配置文件是：redis-shard.conf

下面是配置了两个业务的集群：cacheCluster、activityCluster

    ```
    redis {
        # 默认用于实现缓存
        cacheCluster {
            redis01 {
                ip = "127.0.0.1"
                port = 6379
                password= "k74FkBwb7252FsbNk2M7"
            },
            redis02 {
                ip = "127.0.0.1"
                port = 6379
                password= "k74FkBwb7252FsbNk2M7"
            }
        },
        # 活动开发专用集群
        activityCluster{
            activity01{
                ip = "127.0.0.1"
                port = 6379
            },
            activity02{
                ip = "127.0.0.1"
                port = 6379
            }
        }
    
    }
    ```
    
在使用某个集群几点时，可以这样选择服务
    
    Redis.shardCmd("cacheCluster").get("foo");

 
### Twitter [twemproxy](https://github.com/WangJunTYTL/twemproxy)

twemproxy 是Twitter提供的一种redis集群方式，具体使用可以参照其开源说明文档。

在这种集群中，由于twemproxy提供的是一个代理后的url，我们不用关心在这个url后的redis实例，如果你的业务中用到的是这种redis集群，或者你不需要使用到集群
只是用到单点，可以配置的文件是：redisnodes.properties

    redis01.ip=127.0.0.1
    redis01.port=6379
    #redis01.password=k74FkBwb7252FsbNk2M7
    
    haproxy.ip=127.0.0.1
    haproxy.port=6379
    
比如在这个配置中，我配置了两个节点，在使用时就可以这样使用
    
    Redis.cmd("cacheCluster").get("foo");








