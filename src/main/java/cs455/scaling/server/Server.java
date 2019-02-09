package cs455.scaling.server;

import cs455.scaling.util.Utils;

public class Server {

    public Server(int port, int threadPoolSize, int batchSize, int batchTime) {
        NioServer nioServer = new NioServer(port, threadPoolSize, batchSize, batchTime);
        new Thread(nioServer).start();
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
