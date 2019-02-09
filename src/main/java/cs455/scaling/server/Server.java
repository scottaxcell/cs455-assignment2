package cs455.scaling.server;

import cs455.scaling.threadpool.Task;
import cs455.scaling.threadpool.ThreadPool;
import cs455.scaling.util.Utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server {
    private final int port;
    private final int threadPoolSize;
    private final int batchSize;
    private final int batchTime;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private SelectionKey selectionKey;
    private ThreadPool threadPool;

    public Server(int port, int threadPoolSize, int batchSize, int batchTime) {
        this.port = port;
        this.threadPoolSize = threadPoolSize;
        this.batchSize = batchSize;
        this.batchTime = batchTime;
        threadPool = new ThreadPool(threadPoolSize);
        threadPool.start();
        initServerSocketChannel();
        handleSelectedKeys();
    }

    private void handleSelectedKeys() {
        while (true) {
            try {
//                if (selector.selectNow() == 0)
//                    continue;
                selector.select(); // blocks until a channel is ready

                Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();
                while (selectionKeyIterator.hasNext()) {
                    SelectionKey selectionKey = selectionKeyIterator.next();
                    selectionKeyIterator.remove();
                    if (!selectionKey.isValid())
                        continue;
                    if (selectionKey.isReadable())
                        handleReadableSelectionKey(selectionKey);
                    else if (selectionKey.isWritable())
                        handleWritableSelectionKey(selectionKey);
                    else if (selectionKey.isAcceptable())
                        handleAcceptableSelectionKey(selectionKey);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleAcceptableSelectionKey(SelectionKey selectionKey) {
        Utils.debug("Server.handleAcceptableSelectionKey");
        try {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleWritableSelectionKey(SelectionKey selectionKey) {
//        Utils.debug("Server.handleWritableSelectionKey");
    }

    private void handleReadableSelectionKey(SelectionKey selectionKey) {
        Utils.debug("Server.handleReadableSelectionKey");
        threadPool.execute(new Task() {
            @Override
            public void run() {
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                byte[] bytes = Utils.readBytesFromChannel(socketChannel, Utils.EIGHT_KB);
                String hashCode = Utils.createSha1FromBytes(bytes);
                Utils.debug(String.format("received hashCode = %s", hashCode));

                Utils.writeBytesToChannel(socketChannel, hashCode.getBytes());
                super.run();
            }
        });
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

    private static void printUsageAndExit() {
        Utils.out("USAGE: java cs455.scaling.server.Server portnum thread-pool-size batch-size batch-time\n");
        System.exit(-1);
    }

    public static void main(String[] args) {
        if (args.length != 4)
            printUsageAndExit();

        new Server(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
    }
}
