package cs455.scaling.threadpool;

import cs455.scaling.util.Utils;
import org.junit.Test;

public class ThreadPoolMgrTest {

    @Test
    public void test() throws InterruptedException {
        ThreadPoolMgr threadPoolMgr = new ThreadPoolMgr(4);
        threadPoolMgr.start();
        threadPoolMgr.execute(new Task());
        threadPoolMgr.execute(new Task());
        threadPoolMgr.execute(new Task());
        threadPoolMgr.execute(new Task());
        threadPoolMgr.execute(new Task());

        Thread.sleep(3000);
        Utils.debug("done");
    }

}