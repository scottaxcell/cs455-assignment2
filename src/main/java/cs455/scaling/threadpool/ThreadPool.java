package cs455.scaling.threadpool;

public class ThreadPool extends Thread {
    private final int numThreads;
    private final WorkerQueue workerQueue = new WorkerQueue();
    private final TaskQueue taskQueue = new TaskQueue();

    public ThreadPool(int numThreads) {
        this.numThreads = numThreads;
        initWorkers();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Task task = getTask();
            Worker worker = getWorker();
            worker.setTask(task);
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
    private Worker getWorker() {
        return workerQueue.take();
    }

    /**
     * Returns a task, waiting if necessary until a task becomes available
     */
    private Task getTask() {
        return taskQueue.take();
    }

    /**
     * Executes the given task sometime in the future. The task
     * will execute in an existing pooled thread.
     */
    public void execute(Task task) {
        taskQueue.put(task);
    }
}
