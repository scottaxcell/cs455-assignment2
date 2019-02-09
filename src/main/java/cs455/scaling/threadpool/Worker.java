package cs455.scaling.threadpool;

public class Worker extends Thread {
    private final WorkerQueue workerQueue;
    private Task task;

    public Worker(String name, WorkerQueue workerQueue) {
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
                workerQueue.put(this);

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
