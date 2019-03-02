package cs455.scaling.server;

import cs455.scaling.threadpool.BatchTask;
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
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioServer implements Runnable {
    private final int port;
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
                selector.select();

                Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();
                while (selectionKeyIterator.hasNext()) {
                    SelectionKey selectionKey = selectionKeyIterator.next();
                    selectionKeyIterator.remove();
                    if (!selectionKey.isValid())
                        continue;
                    if (selectionKey.isReadable())
                        handleRead(selectionKey);
                    else if (selectionKey.isWritable())
                        handleWrite(selectionKey);
                    else if (selectionKey.isAcceptable())
                        handleAccept(selectionKey);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleAccept(SelectionKey selectionKey) throws IOException {
//        Utils.debug("Server.handleAccept");

        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        SelectionKey register = socketChannel.register(selector, SelectionKey.OP_READ);
        ThroughputStatistics throughputStatistics = new ThroughputStatistics(throughputStatisticsMgr);
        register.attach(throughputStatistics);
        throughputStatisticsMgr.addThroughputStatistics(throughputStatistics);
    }

    private void handleWrite(SelectionKey selectionKey) {
//        Utils.debug("Server.handleWrite");
    }

    private void handleRead(SelectionKey selectionKey) {
//        Utils.debug(String.format("Server.handleRead key = %s", selectionKey));
//        selectionKey.interestOps(SelectionKey.OP_WRITE);
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        byte[] bytes = null;
        try {
            bytes = Utils.readBytesFromChannel(socketChannel, Utils.EIGHT_KB);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        threadPoolMgr.execute(new BatchTask(selectionKey, bytes));
//        threadPoolMgr.execute(new Task() {
//            @Override
//            public void run() {
//                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
//                byte[] bytes = null;
//                try {
//                    bytes = Utils.readBytesFromChannel(socketChannel, Utils.EIGHT_KB);
//                }
//                catch (IOException e) {
//                    e.printStackTrace();
//                    System.exit(-1);
//                }
//                String hashCode = Utils.createSha1FromBytes(bytes);
////                Utils.debug(String.format("received hashCode = %s", hashCode));
//
//                Utils.writeBytesToChannel(socketChannel, Arrays.copyOfRange(hashCode.getBytes(), 0, Utils.HASH_CODE_BYTE_SIZE));
//                selectionKey.interestOps(SelectionKey.OP_READ);
//                super.run();
//            }
//        });
    }

    private void initServerSocketChannel() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            selectionKey = serverSocketChannel.register(selector = Selector.open(), SelectionKey.OP_ACCEPT, null);
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            Utils.info(String.format("Server started %s:%d", InetAddress.getLocalHost().getCanonicalHostName(), port));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
