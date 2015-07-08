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
