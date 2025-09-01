package com.scnu.threadpool;

import java.util.List;

public interface ThreadPool {
    // 提交任务到线程池
    void execute(Runnable task) throws IllegalAccessException;
    // 优雅的关闭线程池
    void shutdown();
    // 立即关闭
    List<Runnable> shutdownNow();
}
