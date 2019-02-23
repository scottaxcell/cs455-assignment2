package cs455.scaling.util;

import java.util.concurrent.atomic.AtomicInteger;

public class ThroughputStatistics {
    private final AtomicInteger numMessages = new AtomicInteger(0);
    private final ThroughputStatisticsMgr throughputStatisticsMgr;

    public ThroughputStatistics(ThroughputStatisticsMgr throughputStatisticsMgr) {
        this.throughputStatisticsMgr = throughputStatisticsMgr;
        this.throughputStatisticsMgr.incrementNumActiveClients();
    }

    public void incrementNumMessages() {
        numMessages.getAndIncrement();
        throughputStatisticsMgr.incrementNumMessages();
    }

    int getNumMessages() {
        return numMessages.get();
    }

    void reset() {
        numMessages.set(0);
    }
}
