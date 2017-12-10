package bgu.spl.a2;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PromiseTest {

    private Promise<Integer> promise;

    @Before
    public void setUp() throws Exception {
        this.promise = new Promise<>();
    }

    @Test
    public void get() {
        Integer answer;
        try {
            answer = promise.get();
            Assert.fail();
        } catch (IllegalStateException ex1){
            promise.resolve(0);
            try {
                answer = promise.get();
                assertEquals(new Integer(0), answer);
            } catch (IllegalStateException ex2){
                Assert.fail();
            } catch (Exception ex3){
                ex3.printStackTrace();
            }
        }
    }

    @Test
    public void isResolved() {
        try {
            assertEquals(false, promise.isResolved());
            promise.resolve(0);
            assertEquals(true, promise.isResolved());
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Test
    public void resolve() {
        try{
            promise.resolve(0);
            try{
                promise.resolve(1);
                Assert.fail();
            } catch (IllegalStateException ex){
                Integer current = promise.get();
                assertEquals(new Integer(0), current);
            } catch (Exception ex){
                Assert.fail();
            }
        } catch (Exception ex){
            Assert.fail();
        }
    }

    @Test
    public void subscribe() {
        final int[] callbackCounter = {0};
        callback sub = new callback() {
            @Override
            public void call() {
                callbackCounter[0]++;
            }
        };
        promise.subscribe(sub);
        promise.subscribe(sub);
        try {
            promise.resolve(0);
        } catch (Exception ex){
            ex.printStackTrace();
            Assert.fail();
        }
        if(callbackCounter[0] != 2){
            Assert.fail();
        }
        promise.subscribe(sub);
        if(callbackCounter[0] != 3){
            Assert.fail();
        }
    }
}