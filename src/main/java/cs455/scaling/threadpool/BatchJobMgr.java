package cs455.scaling.threadpool;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class BatchJobMgr {
    private final int batchSize;
    private final BlockingQueue<BatchJob> batchJobs = new LinkedBlockingQueue<>();

    public BatchJobMgr(int batchSize) {
        this.batchSize = batchSize;
    }

    synchronized void addBatchTask(BatchTask batchTask) {
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
    }

    synchronized BatchJob getBatchJob() {
        return batchJobs.poll();
    }
}
