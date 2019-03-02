package cs455.scaling.threadpool;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

class BatchJobMgr {
    private final int batchSize;
    private final int batchTime;
    private final BlockingQueue<BatchJob> batchJobs = new LinkedBlockingQueue<>();
    private AtomicBoolean batchJobReady = new AtomicBoolean(false);
    private AtomicBoolean batchJobTimerReady = new AtomicBoolean(false);
    private Timer batchTimer = new Timer(true);

    BatchJobMgr(int batchSize, int batchTime) {
        this.batchSize = batchSize;
        this.batchTime = batchTime;
    }

    private void scheduleBatchTimeTimer() {
        int batchTimeSeconds = batchTime * 1000;
        BatchTimeTimer batchTimeTimer = new BatchTimeTimer();
        batchTimer.cancel();
        batchTimer = new Timer(true);
        batchTimer.schedule(batchTimeTimer, batchTimeSeconds);
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
        while (!batchJobReady.get() && !batchJobTimerReady.get()) {
            try {
                synchronized (this) { // prevents IllegalMonitorStateException
                    wait();
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (batchJobs.peek() == null) {
                batchJobReady.set(false);
                batchJobTimerReady.set(false);
            }
        }

        BatchJob batchJob = batchJobs.poll();

        if (batchJobReady.get())
            batchJobReady.set(false);

        if (batchJobTimerReady.get())
            batchJobTimerReady.set(false);

        scheduleBatchTimeTimer();

        return batchJob;
    }

    private class BatchTimeTimer extends TimerTask {
        @Override
        public void run() {
            batchJobTimerReady.set(true);
            synchronized (BatchJobMgr.this) { // prevents IllegalMonitorStateException
                BatchJobMgr.this.notify();
            }
        }
    }
}
