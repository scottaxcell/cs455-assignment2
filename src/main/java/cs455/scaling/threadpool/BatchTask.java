package cs455.scaling.threadpool;

import cs455.scaling.util.ThroughputStatistics;
import cs455.scaling.util.Utils;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class BatchTask implements Runnable {
    private SelectionKey selectionKey;
    private byte[] data;

    public BatchTask(SelectionKey selectionKey, byte[] data) {
        this.selectionKey = selectionKey;
        this.data = data;
    }

    @Override
    public void run() {
        selectionKey.interestOps(SelectionKey.OP_WRITE);
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        String hashCode = Utils.createSha1FromBytes(data);
//                Utils.debug(String.format("received hashCode = %s", hashCode));
        Utils.writeBytesToChannel(socketChannel, Utils.padHashCodeWithZeros(hashCode).getBytes());
        selectionKey.interestOps(SelectionKey.OP_READ);
//        Utils.debug(String.format("%s executed", this));
        ThroughputStatistics throughputStatistics = (ThroughputStatistics) selectionKey.attachment();
        throughputStatistics.incrementNumMessages();
    }
}
