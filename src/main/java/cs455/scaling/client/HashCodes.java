package cs455.scaling.client;

import cs455.scaling.util.Utils;

import java.util.LinkedList;

public class HashCodes {
    private LinkedList<String> hashCodes = new LinkedList<>();

    public void put(String hashCode) {
        synchronized (hashCodes) {
            hashCodes.push(hashCode);
            Utils.debug(String.format("%s put in hashCodes; %d hashCodes", hashCode, hashCodes.size()));
        }
    }

    public boolean remove(String hashCode) {
        synchronized (hashCodes) {
            boolean removed = hashCodes.remove(hashCode);
            Utils.debug(String.format("%s removed (%s) from hashCodes; %d hashCodes", hashCode, removed, hashCodes.size()));
            return removed;
        }
    }
}
