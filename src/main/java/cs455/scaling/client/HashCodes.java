package cs455.scaling.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class HashCodes {
    private BlockingQueue<String> hashCodes = new LinkedBlockingQueue<>();

    public void put(String hashCode) throws InterruptedException {
        hashCodes.put(hashCode);
//        Utils.debug(String.format("%s put in hashCodes; %d hashCodes", hashCode, hashCodes.size()));
    }

    public boolean remove(String hashCode) {
        boolean removed = hashCodes.remove(hashCode);
//        Utils.debug(String.format("%s removed (%s) from hashCodes; %d hashCodes", hashCode, removed, hashCodes.size()));
        return removed;
    }
}
