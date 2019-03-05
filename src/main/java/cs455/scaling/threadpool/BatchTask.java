package cs455.scaling.threadpool;

import java.nio.channels.SelectionKey;

abstract class BatchTask implements Runnable {
    final SelectionKey selectionKey;

    BatchTask(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }
}
