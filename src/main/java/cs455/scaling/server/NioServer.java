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
import java.util.Arrays;
import java.util.Iterator;

public class NioServer implements Runnable {
    private final int port;
    private final int threadPoolSize;
    private final int batchSize;
    private final int batchTime;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private SelectionKey selectionKey;
        private ThreadPool threadPool;
//    private Executor threadPool;

    public NioServer(int port, int threadPoolSize, int batchSize, int batchTime) {
        this.port = port;
        this.threadPoolSize = threadPoolSize;
        this.batchSize = batchSize;
        this.batchTime = batchTime;
//        threadPool = Executors.newFixedThreadPool(threadPoolSize);
        threadPool = new ThreadPool(threadPoolSize);
        threadPool.start();
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
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private void handleWrite(SelectionKey selectionKey) {
//        Utils.debug("Server.handleWrite");
    }

    private void handleRead(SelectionKey selectionKey) {
//        Utils.debug(String.format("Server.handleRead key = %s", selectionKey));
        selectionKey.interestOps(SelectionKey.OP_WRITE);
        threadPool.execute(new Task() {
            @Override
            public void run() {
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                byte[] bytes = null;
                try {
                    bytes = Utils.readBytesFromChannel(socketChannel, Utils.EIGHT_KB);
                }
                catch (IOException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
                String hashCode = Utils.createSha1FromBytes(bytes);
//                Utils.debug(String.format("received hashCode = %s", hashCode));

                Utils.writeBytesToChannel(socketChannel, Arrays.copyOfRange(hashCode.getBytes(), 0, Utils.HASH_CODE_BYTE_SIZE));
                selectionKey.interestOps(SelectionKey.OP_READ);
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
}
