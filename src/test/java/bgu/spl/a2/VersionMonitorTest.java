package bgu.spl.a2;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class VersionMonitorTest {

    private VersionMonitor vm;

    @Before
    public void setUp() throws Exception {
        this.vm = new VersionMonitor();
    }

    @Test
    public void getVersion() {
        try{
            int v = vm.getVersion();
        } catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void inc() {
        try {
            int expected = vm.getVersion() + 1;
            vm.inc();
            assertEquals(expected, vm.getVersion());
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Test
    public void await() {
        final boolean[] inter = {false};
        Runnable await = new Runnable() {
            public void run() {
                try {
                    int v = vm.getVersion();
                    vm.await(v);
                } catch (InterruptedException ex) {
                    inter[0] = true;
                }
            }
        };
        Thread t = new Thread(await);
        t.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        vm.inc();
        try {
            Thread.sleep(1000);
            if(!inter[0]){
                Assert.fail();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}