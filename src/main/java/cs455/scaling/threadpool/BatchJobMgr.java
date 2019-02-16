package cs455.scaling.threadpool;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

class BatchJobMgr {
    private final int batchSize;
    private final BlockingQueue<BatchJob> batchJobs = new LinkedBlockingQueue<>();
    private AtomicBoolean batchJobReady = new AtomicBoolean(false);

    BatchJobMgr(int batchSize, int batchTime) {
        this.batchSize = batchSize;
        scheduleBatchTimeTimer(batchTime);
    }

    private void scheduleBatchTimeTimer(int batchTime) {
        int batchTimeSeconds = batchTime * 1000;
        BatchTimeTimer batchTimeTimer = new BatchTimeTimer();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(batchTimeTimer, batchTimeSeconds, batchTimeSeconds);
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
            if (batchJobs.peek() == null)
                batchJobReady.set(false);
        }
        batchJobReady.set(false);
        return batchJobs.poll();
    }

    private class BatchTimeTimer extends TimerTask {
        @Override
        public void run() {
            batchJobReady.set(true);
            synchronized (BatchJobMgr.this) { // prevents IllegalMonitorStateException
                BatchJobMgr.this.notify();
            }
        }
    }
}
