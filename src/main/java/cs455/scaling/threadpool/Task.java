package cs455.scaling.threadpool;

import cs455.scaling.util.Utils;

public class Task implements Runnable {
    @Override
    public void run() {
        Utils.debug(String.format("%s executed", this));
    }
}
