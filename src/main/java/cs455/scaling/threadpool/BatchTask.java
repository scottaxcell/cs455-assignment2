package cs455.scaling.threadpool;

import java.nio.channels.SelectionKey;

public abstract class BatchTask implements Runnable {
    protected SelectionKey selectionKey;

    public BatchTask(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }
}
