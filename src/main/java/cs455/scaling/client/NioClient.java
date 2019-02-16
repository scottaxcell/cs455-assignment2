package cs455.scaling.client;

import cs455.scaling.util.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
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
                        handleConnect(selectionKey);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    private void handleConnect(SelectionKey selectionKey) throws IOException {
//        Utils.debug("Client.handleConnect");
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        socketChannel.finishConnect();
        selectionKey.interestOps(SelectionKey.OP_READ);

        Runnable runnable = () -> {
            Random random = new Random();
            byte[] randomBytes = new byte[Utils.EIGHT_KB];

            int debugCount = 0;
            while (!Thread.currentThread().isInterrupted()) {
                random.nextBytes(randomBytes);
                String hashCode = Utils.createSha1FromBytes(randomBytes);
                try {
                    hashCodes.put(new String(Arrays.copyOfRange(hashCode.getBytes(), 0, Utils.HASH_CODE_BYTE_SIZE)));
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Utils.writeBytesToChannel(socketChannel, randomBytes);
                Utils.debug(String.format("sent hashCode = %s (%d)", hashCode, hashCode.length()));

                try {
                    // TODO enable messageRate
                    Thread.sleep(1000 / messageRate);
//                    Thread.sleep(3000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
                debugCount++;
                if (debugCount > 40)
                    break;
            }
        };
        new Thread(runnable).start();
    }

    private void handleRead(SelectionKey selectionKey) throws IOException {
//        Utils.debug("Client.handleRead");
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        byte[] bytes = Utils.readBytesFromChannel(socketChannel, Utils.HASH_CODE_BYTE_SIZE);
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
            System.exit(-1);
        }
    }

}
