package cs455.scaling.client;

import java.util.LinkedList;

public class HashCodes {
    private LinkedList<String> hashCodes = new LinkedList<>();

    public synchronized void put(String hashCode) {
        hashCodes.push(hashCode);
    }

    public synchronized boolean remove(String hashCode) {
        return hashCodes.remove(hashCode);
    }
}
