package cs455.scaling.client;

import cs455.scaling.util.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Random;

public class NioClient implements Runnable {
    private final String serverHost;
    private final int serverPort;
    private final int messageRate;
    private Selector selector;
    private SocketChannel socketChannel;
    private SelectionKey selectionKey;
    private HashCodes hashCodes = new HashCodes();

    public NioClient(String serverHost, int serverPort, int messageRate) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.messageRate = messageRate;
    }

    @Override
    public void run() {
        connectToServer();
        while (true) {
            try {
                selector.select();

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    if (!selectionKey.isValid())
                        continue;
                    if (selectionKey.isReadable())
                        handleRead(selectionKey);
                    else if (selectionKey.isConnectable())
                        handleConnectableSelectionKey(selectionKey);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleConnectableSelectionKey(SelectionKey selectionKey) throws IOException {
        Utils.debug("Client.handleConnectableSelectionKey");
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        socketChannel.finishConnect();
        selectionKey.interestOps(SelectionKey.OP_READ);

        Runnable runnable = () -> {
            Random random = new Random();
            byte[] randomBytes = new byte[Utils.EIGHT_KB];

//            while (!Thread.currentThread().isInterrupted()) {
            random.nextBytes(randomBytes);
            String hashCode = Utils.createSha1FromBytes(randomBytes);
            hashCodes.put(Utils.createSha1FromBytes(randomBytes));
            Utils.writeBytesToChannel(socketChannel, randomBytes);
            Utils.debug(String.format("sent hashCode = %s", hashCode));

            try {
                // TODO enable messageRate
//                    Thread.sleep(1000 / messageRate);
                Thread.sleep(3000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
//            }
        };
        new Thread(runnable).start();
    }

    private void handleRead(SelectionKey selectionKey) {
        Utils.debug("Client.handleRead");
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        byte[] bytes = Utils.readBytesFromChannel(socketChannel, Utils.FORTY_B);
//        String hashCode = Utils.createSha1FromBytes(bytes);
        String hashCode = new String(bytes);
        Utils.debug(String.format("read hashCode = %s", hashCode));
        hashCodes.remove(hashCode);
    }

    private void connectToServer() {
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            selectionKey = socketChannel.register(selector = Selector.open(), SelectionKey.OP_CONNECT);
            socketChannel.connect(new InetSocketAddress(serverHost, serverPort));
            Utils.info(String.format("Connected to server %s:%d", socketChannel.socket().getInetAddress().getCanonicalHostName(), serverPort));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
