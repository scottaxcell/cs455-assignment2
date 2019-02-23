package cs455.scaling.client;

import cs455.scaling.util.ThroughputStatisticsMgr;
import cs455.scaling.util.Utils;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import static cs455.scaling.util.Utils.SIMPLE_DATE_FORMAT;

public class TransmissionStatistics {
    private final int TRANSMISSION_STATISTICS_DELAY = 20 * 1000;
    private AtomicInteger numMessagesSent = new AtomicInteger(0);
    private AtomicInteger numMessagesReceived = new AtomicInteger(0);

    public TransmissionStatistics() {
        TransmissionStatisticsTimer transmissionStatisticsTimer = new TransmissionStatisticsTimer();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(transmissionStatisticsTimer, TRANSMISSION_STATISTICS_DELAY, TRANSMISSION_STATISTICS_DELAY);
    }

    void incrementNumMessagesSent() {
        numMessagesSent.getAndIncrement();
    }

    void incrementNumMessagesReceived() {
        numMessagesReceived.getAndIncrement();
    }

    public int getNumMessagesSent() {
        return numMessagesSent.get();
    }

    public int getNumMessagesReceived() {
        return numMessagesReceived.get();
    }

    void reset() {
        numMessagesSent.set(0);
        numMessagesReceived.set(0);
    }

    private void printStatistics() {
        String timeStamp = String.format("[%s]", SIMPLE_DATE_FORMAT.format(new Date()));
        String sentMsg = String.format("Total Sent Count: %d", getNumMessagesSent());
        String receivedMsg = String.format("Total Received Count: %d", getNumMessagesReceived());
        Utils.out(String.format("%s %s, %s\n", timeStamp, sentMsg, receivedMsg));
    }

    private class TransmissionStatisticsTimer extends TimerTask {
        @Override
        public void run() {
            printStatistics();
            reset();
        }
    }
}
