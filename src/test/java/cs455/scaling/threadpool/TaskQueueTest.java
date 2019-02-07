package cs455.scaling.threadpool;

import cs455.scaling.util.Utils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TaskQueueTest {

    @Test
    public void testThreading() throws InterruptedException {
        final int[] takeCount = {0};
        TaskQueue tq = new TaskQueue();

        Runnable taker = new Runnable() {
            @Override
            public void run() {
                Object object = tq.take();
                assertNotNull(object);
                Utils.debug(object);
                takeCount[0]++;

                object = tq.take();
                assertNotNull(object);
                Utils.debug(object);
                takeCount[0]++;

                object = tq.take();
                assertNotNull(object);
                Utils.debug(object);
                takeCount[0]++;
            }
        };
        Thread takerThread = new Thread(taker);
        takerThread.start();

        Runnable putter = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tq.put("Object 1");
                try {
                    Thread.sleep(2000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tq.put("Object 2");
                try {
                    Thread.sleep(3000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tq.put("Object 3");
            }
        };
        Thread putterThread = new Thread(putter);
        putterThread.start();

        Thread.sleep(7000);

        assertEquals(3, takeCount[0]);
    }
}