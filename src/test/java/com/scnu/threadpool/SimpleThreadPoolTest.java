package com.scnu.threadpool;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

public class SimpleThreadPoolTest {

    @Test
    void testExecuteTask() throws InterruptedException {
        ThreadPool pool = new SimpleThreadPool(1);
        CountDownLatch latch = new CountDownLatch(1);

        try {
            pool.execute(() -> {
                // Task logic here
                latch.countDown();
            });
        } catch (IllegalAccessException e) {
            fail("Should not throw IllegalAccessException");
        }

        boolean completed = latch.await(1, TimeUnit.SECONDS);
        assertTrue(completed, "Task should complete within 1 second");
        pool.shutdown();
    }

    @Test
    void testMultipleTasksExecution() throws InterruptedException {
        int numThreads = 5;
        int numTasks = 20;
        ThreadPool pool = new SimpleThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numTasks);
        AtomicInteger counter = new AtomicInteger(0);

        for (int i = 0; i < numTasks; i++) {
            try {
                pool.execute(() -> {
                    counter.incrementAndGet();
                    latch.countDown();
                });
            } catch (IllegalAccessException e) {
                fail("Should not throw IllegalAccessException");
            }
        }

        boolean completed = latch.await(5, TimeUnit.SECONDS);
        assertTrue(completed, "All tasks should complete within 5 seconds");
        assertEquals(numTasks, counter.get(), "Counter should be equal to the number of tasks");
        pool.shutdown();
    }

    @Test
    void testShutdownNow() throws IllegalAccessException {
        ThreadPool pool = new SimpleThreadPool(2);
        for (int i = 0; i < 5; i++) {
            pool.execute(() -> {
                try {
                    Thread.sleep(1000); // Make task long enough to be interrupted
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        List<Runnable> unexecutedTasks = pool.shutdownNow();

        assertNotNull(unexecutedTasks);
        // Depending on timing, 1 or 2 tasks may have started. 
        // The rest should be in the queue.
        assertTrue(unexecutedTasks.size() >= 3 && unexecutedTasks.size() <= 5, 
            "shutdownNow should return the list of unexecuted tasks");
    }
    
    @Test
    void testShutdownPreventsNewTasks() throws InterruptedException {
        ThreadPool pool = new SimpleThreadPool(1);
        pool.shutdown();
        
        // Let's wait a bit to ensure the shutdown signal has propagated
        TimeUnit.MILLISECONDS.sleep(100);

        assertThrows(IllegalAccessException.class, () -> {
            pool.execute(() -> System.out.println("This should not run"));
        }, "Should throw IllegalAccessException when trying to execute a task on a shutdown pool");
    }
}
