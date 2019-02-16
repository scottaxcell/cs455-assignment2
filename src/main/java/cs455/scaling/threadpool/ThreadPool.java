package cs455.scaling.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class ThreadPool {
    private final BlockingQueue<Worker> workerQueue = new LinkedBlockingQueue<>();

    ThreadPool(int threadPoolSize) {
        initWorkers(threadPoolSize);
    }

    private void initWorkers(int threadPoolSize) {
        for (int i = 0; i < threadPoolSize; i++)
            new Worker(String.format("Worker %d", i), workerQueue).start();
    }

    Worker getWorker() throws InterruptedException {
        return workerQueue.take();
    }
}
