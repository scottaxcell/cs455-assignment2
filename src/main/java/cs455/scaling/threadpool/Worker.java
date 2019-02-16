package cs455.scaling.threadpool;

import java.util.concurrent.BlockingQueue;

public class Worker extends Thread {
    private final BlockingQueue<Worker> workerQueue;
    private Runnable job;

    Worker(String name, BlockingQueue<Worker> workerQueue) {
        super(name);
        this.workerQueue = workerQueue;
    }

    synchronized void setJob(Runnable job) {
        this.job = job;
        notify();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (this) { // prevents IllegalMonitorStateException
                try {
                    workerQueue.put(this);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (job == null) {
                    try {
                        wait();
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                job.run();
                job = null;
            }
        }
    }
}
