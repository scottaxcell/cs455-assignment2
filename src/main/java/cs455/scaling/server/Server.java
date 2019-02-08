package cs455.scaling.server;

import cs455.scaling.threadpool.ThreadPool;
import cs455.scaling.util.Utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class Server {
    private final int port;
    private final int numThreads;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private SelectionKey selectionKey;
    private ThreadPool threadPool;

    public Server(int port, int numThreads) {
        this.port = port;
        this.numThreads = numThreads;
        threadPool = new ThreadPool(numThreads);
        threadPool.start();
        initServerSocketChannel();
        select();
    }

    private void select() {
        while (true) {
            try {
                if (selector.selectNow() == 0)
                    continue;

                for (SelectionKey selectionKey : selector.selectedKeys()) {
                    if (!selectionKey.isValid())
                        continue;
                    if (selectionKey.isReadable())
                        handleReadableSelectionKey(selectionKey);
                    else if (selectionKey.isWritable())
                        handleWritableSelectionKey(selectionKey);
                    else if (selectionKey.isAcceptable())
                        handleAcceptableSelectionKey(selectionKey);
                    else if (selectionKey.isConnectable())
                        handleConnectableSelectionKey(selectionKey);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleConnectableSelectionKey(SelectionKey selectionKey) {
        
    }

    private void handleAcceptableSelectionKey(SelectionKey selectionKey) {

    }

    private void handleWritableSelectionKey(SelectionKey selectionKey) {

    }

    private void handleReadableSelectionKey(SelectionKey selectionKey) {

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

    public static void main(String[] args) {
        // TODO handle args
        new Server(50731, 4);
    }
}
