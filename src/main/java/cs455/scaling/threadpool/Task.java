package cs455.scaling.threadpool;

import cs455.scaling.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Task implements Runnable {
    @Override
    public void run() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.S");
        Utils.debug(String.format("task executed @ %s", simpleDateFormat.format(date)));
    }
}
