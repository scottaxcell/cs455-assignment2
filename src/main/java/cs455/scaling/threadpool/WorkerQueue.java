package cs455.scaling.threadpool;

import java.util.LinkedList;

public class WorkerQueue {
    LinkedList<Worker> queue = new LinkedList<>();

    public synchronized Worker take() {
        while (queue.isEmpty()) {
            try {
                wait();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return queue.pollFirst();
    }

    public synchronized void put(Worker worker) {
        queue.addLast(worker);
        notify();
    }
}
