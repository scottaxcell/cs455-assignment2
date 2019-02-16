package cs455.scaling.threadpool;

import java.util.concurrent.BlockingQueue;

public class Worker extends Thread {
    private final BlockingQueue<Worker> workerQueue;
    private Task task;

    public Worker(String name, BlockingQueue<Worker> workerQueue) {
        super(name);
        this.workerQueue = workerQueue;
    }

    public synchronized void setTask(Task task) {
        this.task = task;
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

                while (task == null) {
                    try {
                        wait();
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                task.run();

                task = null;
            }
        }
    }
}
