package cs455.scaling.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class HashCodes {
    private BlockingQueue<String> hashCodes = new LinkedBlockingQueue<>();

    public void put(String hashCode) throws InterruptedException {
        hashCodes.put(hashCode);
    }

    public boolean remove(String hashCode) {
        return hashCodes.remove(hashCode);
    }
}
