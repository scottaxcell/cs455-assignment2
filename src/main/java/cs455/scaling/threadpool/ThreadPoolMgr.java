package cs455.scaling.threadpool;

public class ThreadPoolMgr extends Thread {
    private final ThreadPool threadPool;
    private final BatchJobMgr batchJobMgr;

    public ThreadPoolMgr(int threadPoolSize, int batchSize, int batchTime) {
        threadPool = new ThreadPool(threadPoolSize);
        batchJobMgr = new BatchJobMgr(batchSize, batchTime);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Worker worker = getWorker();
                BatchJob batchJob = getBatchJob();
                worker.setJob(batchJob);
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
    private BatchJob getBatchJob() {
        return batchJobMgr.getBatchJob();
    }

    /**
     * Executes the given task sometime in the future
     */
    public void execute(BatchTask batchTask) {
        batchJobMgr.addBatchTask(batchTask);
    }

    /**
     * Executes the given task "immediately"
     */
    public void executeImmediately(BatchTask batchTask) throws InterruptedException {
        Worker worker = getWorker();
        worker.setJob(batchTask);
    }
}
