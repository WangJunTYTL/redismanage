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
 
在java jedis组件中提供了一种集群方式：ShardJedis。它采用对key进行hash计算确定该key应该落到的节点

下面是配置了两个业务的集群：cacheCluster、activityCluster

     shard {
       # cache cluster01
       cacheCluster01: ["127.0.0.1:6379", "127.0.0.1:6379:k74FkBwb7252FsbNk2M7"]
       # cache cluster02
       cacheCluster02: ["127.0.0.1:6379", "127.0.0.1:6379:k74FkBwb7252FsbNk2M7"]
     }

在使用某个集群几点时，可以这样选择服务
    
    Redis.shardCmd("cacheCluster").get("foo");

 
### Twitter [twemproxy](https://github.com/WangJunTYTL/twemproxy)

twemproxy 是Twitter提供的一种redis集群方式，具体使用可以参照其开源说明文档。

在这种集群中，由于twemproxy提供的是一个代理后的url，我们不用关心在这个url后的redis实例，如果你的业务中用到的是这种redis集群，你可以通过下面配置选项

     proxy = {
       redis01: "127.0.0.1:6379:k74FkBwb7252FsbNk2M7"
       haproxy: "127.0.0.1:6379"
     }

比如在这个配置中，我配置了两个节点，在使用时就可以这样使用
    
    Redis.cmd("redis01").get("foo");


### 其它配置项说明

除了上面配置redis实例的配置项，还提供了其它一些配置，具体配置可在redis.conf进行更改

```
 # 配置redis连接池
 pool {
    #最大分配的对象数
    maxActive = 2000
    #最大能够保持idel状态的对象数
    maxIdle = 10
    #当池内没有返回对象时，最大等待时间
    maxWait = 5000
    #当调用borrow Object方法时，是否进行有效性检查
    testOnBorrow = false
    #当调用return Object方法时，是否进行有效性检查
    testOnReturn = false
  }

  # 配置命令超时执行时间，如果命令执行超过设定时间,抛出超时异常,避免应用线程被长时间挂起
  future.invoke.timeout = 5s

```






