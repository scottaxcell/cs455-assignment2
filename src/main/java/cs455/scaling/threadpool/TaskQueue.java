package cs455.scaling.threadpool;

import cs455.scaling.util.Utils;

import java.util.LinkedList;

public class TaskQueue {
    LinkedList<Task> queue = new LinkedList<>();

    public synchronized Task take() {
        while (queue.isEmpty()) {
            try {
                wait();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Task task = queue.pollFirst();
        Utils.debug(String.format("%s removed from taskQueue; %d tasks in queue", task, queue.size()));
        return task;
    }

    public synchronized void put(Task task) {
        Utils.debug(String.format("%s added to taskQueue; %d tasks in queue", task, queue.size()));
        queue.addLast(task);
        notify();
    }
}
