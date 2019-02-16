package cs455.scaling.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BatchJob implements Runnable {
    BlockingQueue<BatchTask> batchTasks = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        batchTasks.stream()
            .forEach(BatchTask::run);
    }

    public void addBatchTask(BatchTask batchTask) {
        try {
            batchTasks.put(batchTask);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getBatchTaskSize() {
        return batchTasks.size();
    }
}
