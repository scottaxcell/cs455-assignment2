package cs455.scaling.threadpool;

import org.junit.Test;

import static org.junit.Assert.*;

public class ThreadPoolTest {

    @Test
    public void test() throws InterruptedException {
        ThreadPool threadPool = new ThreadPool(4);
        threadPool.start();
        threadPool.execute(new Task());
        threadPool.execute(new Task());
        threadPool.execute(new Task());
        threadPool.execute(new Task());
        threadPool.execute(new Task());

        Thread.sleep(3000);
    }

}