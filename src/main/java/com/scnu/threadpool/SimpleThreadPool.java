package com.scnu.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class SimpleThreadPool implements ThreadPool {
    private int initialSize;
    // 任务队列
    private BlockingQueue<Runnable> taskQueue;
    // 用于管理工作线程的集合
    private List<WorkerThread> threads;
    // 是否shutdown的标志
    private volatile boolean isShutdown = false;

    public SimpleThreadPool(int initialSize){
        this.initialSize = initialSize;
        taskQueue = new LinkedBlockingDeque<>();
        threads = new ArrayList<>(initialSize);
        // 初始化一定数量的工作线程
        for (int i = 0; i < initialSize; i++){
            WorkerThread workerThread = new WorkerThread(taskQueue);
            workerThread.start();
            threads.add(workerThread);
        }
    }

    /**
     * 将任务放入到任务队列，并让工作线程去执行任务
     * @param task
     */
    @Override
    public void execute(Runnable task) throws IllegalAccessException {
        if(isShutdown){
            throw new IllegalAccessException("ThreadPool");
        }
        taskQueue.offer(task);
    }

    /**
     * 优雅的关闭线程池
     * TODO: 等当前工作线程的任务执行完毕
     */
    @Override
    public void shutdown() {
        isShutdown = true;
        for(WorkerThread workerThread : threads){
            // 中段线程
            workerThread.interrupt();
        }
    }

    /**
     * 关闭所有线程，返回未完成的任务列表
     */
    @Override
    public List<Runnable> shutdownNow() {
        isShutdown = true;
        List<Runnable> remainTasks = new ArrayList<>();
        taskQueue.drainTo(remainTasks);
        for(WorkerThread workerThread : threads){
            // 中段线程
            workerThread.interrupt();
        }
        return remainTasks;
    }
}
