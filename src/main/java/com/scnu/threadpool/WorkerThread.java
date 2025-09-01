package com.scnu.threadpool;

import java.util.concurrent.BlockingQueue;

/**
 * 工作线程类
 */
public class WorkerThread extends Thread {
    // 从任务队列中取出并执行任务
    private BlockingQueue<Runnable> taskQueue;

    public WorkerThread(BlockingQueue<Runnable> taskQueue) {
	this.taskQueue = taskQueue;
    }

    @Override
    public void run() {
	while (!Thread.currentThread().isInterrupted()){
	    try {
		// 从任务队列中取出一个任务，如果队列为空，则阻塞等待
		Runnable task = taskQueue.take();
		//执行任务
		task.run();
	    } catch (InterruptedException e) {
		// 捕获到中断异常，恢复中断状态并退出循环
		Thread.currentThread().interrupt();
		break;
	    }
	}
    }
}
