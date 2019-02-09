package cs455.scaling.client;

import cs455.scaling.util.Utils;

public class Client {

    public Client(String serverHost, int serverPort, int messageRate) {
        NioClient nioClient = new NioClient(serverHost, serverPort, messageRate);
        new Thread(nioClient).start();
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
