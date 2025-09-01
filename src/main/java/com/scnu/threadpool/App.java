package com.scnu.threadpool;

public class App {
    public static void main(String[] args) throws InterruptedException {
        SimpleThreadPool pool = new SimpleThreadPool(5);

        for (int i = 0; i < 10; i++) {
            int taskNum = i;
            Runnable task = () -> {
                System.out.println("Executing task " + taskNum + " by thread " + Thread.currentThread().getName());
            };
            try {
                pool.execute(task);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        //Thread.sleep(100);

    }
}
