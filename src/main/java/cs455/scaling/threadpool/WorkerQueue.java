package cs455.scaling.threadpool;

import cs455.scaling.util.Utils;

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
        Worker worker = queue.pollFirst();
//        Utils.debug(String.format("%s removed from workerQueue; %d workers in queue", worker, queue.size()));
        return worker;
    }

    public synchronized void put(Worker worker) {
//        Utils.debug(String.format("%s added to workerQueue; %d workers in queue", worker, queue.size()));
        queue.addLast(worker);
        notify();
    }
}
