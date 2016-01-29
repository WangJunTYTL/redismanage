package com.peaceful.common.redis.news;

import com.peaceful.common.util.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.*;

/**
 * 利用future不是为了防止网络阻塞，但可以防止因网络阻塞或链接不释放导致调用者被长时间阻塞的问题
 *
 * Created by wangjun on 16/1/29.
 */
public class RedisFutureInvoke extends BasicRedisInvoke {

    Logger logger = LoggerFactory.getLogger(getClass());

    private ExecutorService executorService = Executors.newCachedThreadPool();

    private BasicRedisInvoke basicRedisInvoke = new BasicRedisInvoke();

    /**
     * 执行redis命令前
     *
     * @param invokeContext
     */
    void invokeBefore(InvokeContext invokeContext) {
        try {
            RedisInvokeChain.invokeBefore.execute(invokeContext);
        } catch (Exception e) {
            logger.error("Error: {}", ExceptionUtils.getStackTrace(e));
        }
    }


    @Override
    public Object doInvoke(Method method, Object[] args, int type, String node) throws TimeoutException {

        InvokeContext invokeContext = new InvokeContext();
        invokeContext.put("redis.node", node);
        invokeContext.put("redis.type", type == RedisClientType.PROXY ? "PROXY" : "SHARD");
        invokeContext.put("redis.cmd", method.getName());
        invokeContext.put("redis.args", args);
        invokeBefore(invokeContext);

        FutureTask future = new FutureTask(new FutureInvoke(basicRedisInvoke, method, args, type, node));
        executorService.submit(future);
        Object result;
        try {
            result = future.get(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // 为了方便用户端直接使用服务而不显示地处理异常，异常信息均转为运行期异常
            throw new RuntimeException(ExceptionUtils.getStackTrace(e));
        } catch (ExecutionException e) {
            throw new RuntimeException(ExceptionUtils.getStackTrace(e));
        } catch (TimeoutException e) {
            throw new TimeoutException(String.format("redis cmd: %s invoke timeout at %s node", method.getName(), node));
        }
        invokeContext.put("result", result);
        invokeAfter(invokeContext);

        // redis 连接池监控
        // jedisPool.getNumActive();
        return result;
    }

    /**
     * 执行redis命令后
     *
     * @param invokeContext
     */
    void invokeAfter(InvokeContext invokeContext) {
        try {
            RedisInvokeChain.invokeAfter.execute(invokeContext);
        } catch (Exception e) {
            logger.error("Error: {}", ExceptionUtils.getStackTrace(e));
        }
    }


    private class FutureInvoke implements Callable {

        BasicRedisInvoke redisInvoke;
        Method method;
        Object[] args;
        int type;
        String node;

        public FutureInvoke(BasicRedisInvoke redisInvoke, Method method, Object[] args, int type, String node) {
            this.redisInvoke = redisInvoke;
            this.method = method;
            this.args = args;
            this.type = type;
            this.node = node;
        }

        @Override
        public Object call() throws Exception {
            return redisInvoke.doInvoke(method, args, type, node);
        }
    }
}
