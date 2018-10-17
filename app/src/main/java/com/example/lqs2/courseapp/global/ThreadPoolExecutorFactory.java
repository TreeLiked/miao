package com.example.lqs2.courseapp.global;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 全局线程的管理，避免资源的浪费
 *
 * @author lqs2
 */
public class ThreadPoolExecutorFactory {
    /**
     * CORE_POOL_SIZE 池中所保存的线程数，包括空闲线程。
     */
    private static final int CORE_POOL_SIZE = 40;
    /**
     * MAXIMUM_POOL_SIZE - 池中允许的最大线程数(采用LinkedBlockingQueue时没有作用)。
     */
    private static final int MAXIMUM_POOL_SIZE = 40;
    /**
     * KEEP_ALIVE_TIME -当线程数大于核心时，此为终止前多余的空闲线程等待新任务的最长时间，线程池维护线程所允许的空闲时间
     */
    private static final int KEEP_ALIVE_TIME = 60;

    /**
     * 执行前用于保持任务的队列（缓冲队列）
     */
    private static final int capacity = 300;

    /**
     * 线程池对象
     */
    private static volatile ThreadPoolExecutor threadPoolExecutor = null;

    private ThreadPoolExecutorFactory() {
    }

    /**
     * 线程池单例，使用双重检测锁
     *
     * @return ThreadPoolExecutor 返回单例线程池对象
     */
    public static ThreadPoolExecutor getThreadPoolExecutor() {
        if (null == threadPoolExecutor) {
            ThreadPoolExecutor t;
            synchronized (ThreadPoolExecutorFactory.class) {
                t = threadPoolExecutor;
                if (null == t) {
                    synchronized (ThreadPoolExecutorFactory.class) {
                        t = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new ThreadPoolExecutor.DiscardOldestPolicy());
                    }
                    threadPoolExecutor = t;
                }
            }
        }
        return threadPoolExecutor;
    }
}
