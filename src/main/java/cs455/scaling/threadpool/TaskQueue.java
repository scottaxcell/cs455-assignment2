package cs455.scaling.threadpool;

import java.util.LinkedList;

public class TaskQueue {
    LinkedList<Object> queue = new LinkedList<>();

    public synchronized Object take() {
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

    public synchronized void put(Object object) {
        queue.addLast(object);
        notifyAll();
    }
}
