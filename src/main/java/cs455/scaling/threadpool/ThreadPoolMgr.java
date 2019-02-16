package cs455.scaling.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * -
 */
public class ThreadPoolMgr extends Thread {
    private final int numThreads;
    private final BlockingQueue<Worker> workerQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Task> workUnitQueue = new LinkedBlockingQueue<>();

    public ThreadPoolMgr(int numThreads) {
        this.numThreads = numThreads;
        initWorkers();
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

    private void initWorkers() {
        for (int i = 0; i < numThreads; i++) {
            Worker worker = new Worker(String.format("Worker %d", i), workerQueue);
            worker.start();
        }
    }

    /**
     * Returns a worker, waiting if necessary until a worker becomes available
     */
    private Worker getWorker() throws InterruptedException {
        return workerQueue.take();
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
