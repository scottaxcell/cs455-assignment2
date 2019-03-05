package cs455.scaling.threadpool;

import cs455.scaling.util.ThroughputStatistics;
import cs455.scaling.util.Utils;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ReadBatchTask extends BatchTask {

    public ReadBatchTask(SelectionKey selectionKey) {
        super(selectionKey);
    }

    @Override
    public void run() {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        byte[] data = null;
        try {
            data = Utils.readBytesFromChannel(socketChannel, Utils.EIGHT_KB);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        selectionKey.interestOps(SelectionKey.OP_WRITE);
        String hashCode = Utils.createSha1FromBytes(data);
        Utils.writeBytesToChannel(socketChannel, Utils.padHashCodeWithZeros(hashCode).getBytes());
        selectionKey.interestOps(SelectionKey.OP_READ);
        ThroughputStatistics throughputStatistics = (ThroughputStatistics) selectionKey.attachment();
        throughputStatistics.incrementNumMessages();
    }
}
