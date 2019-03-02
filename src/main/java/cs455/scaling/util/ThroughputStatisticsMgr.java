package cs455.scaling.util;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static cs455.scaling.util.Utils.SIMPLE_DATE_FORMAT;

public class ThroughputStatisticsMgr {
    private final int THROUGHPUT_STATISTICS_DELAY = 20;
    private final AtomicInteger numActiveClients = new AtomicInteger(0);
    private final AtomicInteger numMessages = new AtomicInteger(0);
    private final List<ThroughputStatistics> throughputStatisticsList = new ArrayList<>();

    public ThroughputStatisticsMgr() {
        scheduleThroughputStatisticsTimer();
    }

    private void scheduleThroughputStatisticsTimer() {
        int throughputStatisticsDelaySeconds = THROUGHPUT_STATISTICS_DELAY * 1000;
        ThroughputStatisticsTimer throughputStatisticsTimer = new ThroughputStatisticsTimer();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(throughputStatisticsTimer, throughputStatisticsDelaySeconds, throughputStatisticsDelaySeconds);
    }

    //  Server Throughput: x messages/s,
    //  Active Client Connections: y,
    //  Mean Per-client Throughput: p messages/s,
    //  Std. Dev. Of Per-client Throughput: q messages/s

    private synchronized void printStatistics() {
        String timeStamp = String.format("[%s]", SIMPLE_DATE_FORMAT.format(new Date()));
        String serverThroughput = String.format("Server Throughput: %d messages/s", getServerThroughput());
        String activeClients = String.format("Active Client Connections: %d", getNumActiveClients());
        String meanThroughput = String.format("Mean Per-client Throughput: %.2f messages/s", getMeanPerClientThroughput());
        String stdDevThroughput = String.format("Std. Dev. Of Per-client Throughput: %.2f messages/s", getStdDevPerClientThroughput());
        Utils.out(String.format("%s %s, %s, %s, %s\n", timeStamp, serverThroughput, activeClients, meanThroughput, stdDevThroughput));
    }

    private double getStdDevPerClientThroughput() {
        synchronized (numActiveClients) {
            if (numActiveClients.get() == 0)
                return Double.valueOf(0);
            double mean = getMeanPerClientThroughput();
            double stdDev = 0;
            for (ThroughputStatistics throughputStatistics : throughputStatisticsList)
                stdDev += Math.pow((throughputStatistics.getNumMessages() - mean), 2);
            return Math.sqrt(stdDev / numActiveClients.get()) / THROUGHPUT_STATISTICS_DELAY;
        }
    }

    private double getMeanPerClientThroughput() {
        synchronized (numActiveClients) {
            if (numActiveClients.get() == 0)
                return Double.valueOf(0);
            double sum = 0;
            for (ThroughputStatistics throughputStatistics : throughputStatisticsList)
                sum += throughputStatistics.getNumMessages();
            return (sum / numActiveClients.get()) / THROUGHPUT_STATISTICS_DELAY;
        }
    }

    private int getNumActiveClients() {
        return numActiveClients.get();
    }

    private int getServerThroughput() {
        return numMessages.get() / THROUGHPUT_STATISTICS_DELAY;
    }

    private synchronized void reset() {
        numMessages.set(0);
        throughputStatisticsList.stream()
            .forEach(ThroughputStatistics::reset);
    }

    void incrementNumActiveClients() {
        numActiveClients.getAndIncrement();
    }

    void incrementNumMessages() {
        numMessages.getAndIncrement();
    }

    public synchronized void addThroughputStatistics(ThroughputStatistics throughputStatistics) {
        throughputStatisticsList.add(throughputStatistics);
    }

    private class ThroughputStatisticsTimer extends TimerTask {
        @Override
        public void run() {
            printStatistics();
            reset();
        }
    }
}
