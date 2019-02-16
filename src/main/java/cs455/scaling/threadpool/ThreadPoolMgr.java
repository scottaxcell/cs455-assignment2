package cs455.scaling.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * TODO
 * - create work unit and data packet
 */
public class ThreadPoolMgr extends Thread {
    private final int batchSize;
    private final int batchTime;
    private final ThreadPool threadPool;
    private final BlockingQueue<Task> workUnitQueue = new LinkedBlockingQueue<>();

    public ThreadPoolMgr(int threadPoolSize, int batchSize, int batchTime) {
        this.batchSize = batchSize;
        this.batchTime = batchTime;
        threadPool = new ThreadPool(threadPoolSize);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Task task = getTask();
                Worker worker = getWorker();
                worker.setTask(task);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Returns a worker, waiting if necessary until a worker becomes available
     */
    private Worker getWorker() throws InterruptedException {
        return threadPool.getWorker();
    }

    /**
     * Returns a task, waiting if necessary until a task becomes available
     */
    private Task getTask() throws InterruptedException {
        return workUnitQueue.take();
    }

    /**
     * Executes the given task sometime in the future. The task
     * will execute in an existing pooled thread.
     */
    public void execute(Task task) {
        // TODO add work unit to last work unit if still space
        // otherwise create new work unit and add it to queue
        try {
            workUnitQueue.put(task);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
