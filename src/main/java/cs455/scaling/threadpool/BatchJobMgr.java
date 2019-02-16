package cs455.scaling.threadpool;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

class BatchJobMgr {
    private final int batchSize;
    private AtomicBoolean batchJobReady = new AtomicBoolean(false);
    private final BlockingQueue<BatchJob> batchJobs = new LinkedBlockingQueue<>();

    BatchJobMgr(int batchSize) {
        this.batchSize = batchSize;
    }

    void addBatchTask(BatchTask batchTask) {
        boolean batchTaskAdded = false;
        Iterator<BatchJob> batchJobIterator = batchJobs.iterator();
        while (batchJobIterator.hasNext()) {
            BatchJob batchJob = batchJobIterator.next();
            if (batchJob.getBatchTaskSize() < batchSize) {
                batchJob.addBatchTask(batchTask);
                batchTaskAdded = true;
                break;
            }
        }
        if (!batchTaskAdded) {
            BatchJob batchJob = new BatchJob();
            batchJob.addBatchTask(batchTask);
            try {
                batchJobs.put(batchJob);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        BatchJob batchJob = batchJobs.peek();
        if (batchJob != null && batchJob.getBatchTaskSize() == batchSize) {
            batchJobReady.set(true);
            synchronized (this) { // prevents IllegalMonitorStateException
                notify();
            }
        }
    }

    BatchJob getBatchJob() {
        while (!batchJobReady.get()) {
            try {
                synchronized (this) { // prevents IllegalMonitorStateException
                    wait();
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        batchJobReady.set(false);
        return batchJobs.poll();
    }

    private boolean isBatchJobReady() {
        BatchJob batchJob = batchJobs.peek();
        if (batchJob != null) {
            if (batchJob.getBatchTaskSize() == batchSize)
                return true;
        }
        return false;
    }
}
