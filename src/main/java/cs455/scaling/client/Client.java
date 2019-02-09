package cs455.scaling.client;

import cs455.scaling.util.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Random;

public class Client {
    private final String serverHost;
    private final int serverPort;
    private final int messageRate;
    private Selector selector;
    private SocketChannel socketChannel;
    private SelectionKey selectionKey;
    private HashCodes hashCodes = new HashCodes();

    public Client(String serverHost, int serverPort, int messageRate) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.messageRate = messageRate;
        connectToServer();
        handleSelectedKeys();
    }

    private void handleSelectedKeys() {
        while (true) {
            try {
//                if (selector.selectNow() == 0)
//                    continue;
                selector.select();

                Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();
                while (selectionKeyIterator.hasNext()) {
                    SelectionKey selectionKey = selectionKeyIterator.next();
                    selectionKeyIterator.remove();
                    if (!selectionKey.isValid())
                        continue;
                    if (selectionKey.isReadable())
                        handleReadableSelectionKey(selectionKey);
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
        Utils.debug("Client.handleConnectableSelectionKey");
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        try {
            socketChannel.finishConnect();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        selectionKey.interestOps(SelectionKey.OP_WRITE);

        Runnable runnable = () -> {
            Random random = new Random();
            byte[] randomBytes = new byte[Utils.EIGHT_K];

            while (!Thread.currentThread().isInterrupted()) {
                selectionKey.interestOps(SelectionKey.OP_WRITE);

                random.nextBytes(randomBytes);
                String hashCode = Utils.createSha1FromBytes(randomBytes);
                Utils.debug(String.format("hashCode = %s", hashCode));
                hashCodes.put(Utils.createSha1FromBytes(randomBytes));
                ByteBuffer src = ByteBuffer.wrap(randomBytes);
                try {
                    socketChannel.write(src);
                    Utils.debug("sent random bytes to server");
                }
                catch (IOException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
                try {
                    // TODO enable messageRate
//                    Thread.sleep(1000 / messageRate);
                    Thread.sleep(3000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void handleReadableSelectionKey(SelectionKey selectionKey) {
        // TODO read hashcode from server and remove from list of saved hashcodes
        Utils.debug("Client.handleReadableSelectionKey");
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer dst = ByteBuffer.allocateDirect(Utils.EIGHT_K);
        try {
            socketChannel.read(dst);
            dst.flip();
            byte[] bytes = new byte[dst.remaining()];
            dst.get(bytes);
            String hashCode = Utils.createSha1FromBytes(bytes);
            Utils.debug(String.format("hashCode = %s", hashCode));
            hashCodes.remove(hashCode);

            ByteBuffer src = ByteBuffer.wrap(hashCode.getBytes());
            socketChannel.write(src);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
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

    private static void printUsageAndExit() {
        Utils.out("USAGE: java cs455.scaling.client.Client server-host server-port message-rate\n");
        System.exit(-1);
    }

    public static void main(String[] args) {
        if (args.length != 3)
            printUsageAndExit();

        new Client(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
    }
}
