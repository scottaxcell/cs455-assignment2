package cs455.scaling.client;

import cs455.scaling.util.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

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
                if (selector.selectNow() == 0)
                    continue;

                for (SelectionKey selectionKey : selector.selectedKeys()) {
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

    }

    private void handleReadableSelectionKey(SelectionKey selectionKey) {
        // TODO read hashcode from server and remove from list of saved hashcodes
    }

    private void connectToServer() {
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            selectionKey = socketChannel.register(selector = Selector.open(), SelectionKey.OP_CONNECT);
            socketChannel.connect(new InetSocketAddress(serverHost, serverPort));
            Utils.info(String.format("Connected to server %s:%d", socketChannel.socket().getInetAddress().getCanonicalHostName(), serverPort));
        } catch (IOException e) {
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
