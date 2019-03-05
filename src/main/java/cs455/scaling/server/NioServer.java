package cs455.scaling.server;

import cs455.scaling.threadpool.AcceptBatchTask;
import cs455.scaling.threadpool.ReadBatchTask;
import cs455.scaling.threadpool.ThreadPoolMgr;
import cs455.scaling.util.ThroughputStatistics;
import cs455.scaling.util.ThroughputStatisticsMgr;
import cs455.scaling.util.Utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

public class NioServer implements Runnable {
    private final int port;
    private final ReentrantLock selectorLock = new ReentrantLock();
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private SelectionKey selectionKey;
    private ThreadPoolMgr threadPoolMgr;
    private ThroughputStatisticsMgr throughputStatisticsMgr = new ThroughputStatisticsMgr();

    public NioServer(int port, int threadPoolSize, int batchSize, int batchTime) {
        this.port = port;
        threadPoolMgr = new ThreadPoolMgr(threadPoolSize, batchSize, batchTime);
        threadPoolMgr.start();
    }

    @Override
    public void run() {
        initServerSocketChannel();
        while (true) {
            try {
                selectorLock.lock();
                selectorLock.unlock();
                selector.select(500);

                Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();
                while (selectionKeyIterator.hasNext()) {
                    SelectionKey selectionKey = selectionKeyIterator.next();
                    selectionKeyIterator.remove();
                    if (!selectionKey.isValid())
                        continue;
                    if (selectionKey.isReadable())
                        handleRead(selectionKey);
                    else if (selectionKey.isAcceptable())
                        handleAccept(selectionKey);
                }
            }
            catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleAccept(SelectionKey selectionKey) throws IOException, InterruptedException {
        if (selectionKey.attachment() != null)
            return;

        selectionKey.attach(new Integer(42));
        ThroughputStatistics throughputStatistics = new ThroughputStatistics(throughputStatisticsMgr);
        throughputStatisticsMgr.addThroughputStatistics(throughputStatistics);
        threadPoolMgr.executeImmediately(new AcceptBatchTask(selectionKey, selector, selectorLock, throughputStatistics));
    }

    private void handleRead(SelectionKey selectionKey) {
        selectionKey.interestOps(SelectionKey.OP_WRITE);
        threadPoolMgr.execute(new ReadBatchTask(selectionKey));
    }

    private void initServerSocketChannel() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            selectionKey = serverSocketChannel.register(selector = Selector.open(), SelectionKey.OP_ACCEPT);
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            Utils.info(String.format("Server started %s:%d", InetAddress.getLocalHost().getCanonicalHostName(), port));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
