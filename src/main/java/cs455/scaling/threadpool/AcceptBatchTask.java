package cs455.scaling.threadpool;

import cs455.scaling.util.ThroughputStatistics;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.locks.ReentrantLock;

public class AcceptBatchTask extends BatchTask {
    private final Selector selector;
    private final ReentrantLock selectorLock;
    private final ThroughputStatistics throughputStatistics;

    public AcceptBatchTask(SelectionKey selectionKey, Selector selector, ReentrantLock selectorLock, ThroughputStatistics throughputStatistics) {
        super(selectionKey);
        this.selector = selector;
        this.selectorLock = selectorLock;
        this.throughputStatistics = throughputStatistics;
    }

    @Override
    public void run() {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        try {
            selectorLock.lock();
            selector.wakeup();
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            SelectionKey register = socketChannel.register(selector, SelectionKey.OP_READ);
            register.attach(throughputStatistics);
            selectionKey.attach(null);
            selectorLock.unlock();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
