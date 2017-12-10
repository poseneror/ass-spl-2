package bgu.spl.a2;

import sun.nio.ch.ThreadPool;

import java.util.List;

public class main {
    public static void main(String[] args) {
        ActorThreadPool pool = new ActorThreadPool(1);
        pool.start();
        Action<Integer> myAction = new Action<Integer>() {
            @Override
            protected void start() {
                setActionName("ACTION1");
                Integer ans = 10;
                complete(ans);
            }
        };
        pool.submit(myAction, "or", new PrivateState(){});
        myAction.getResult().subscribe(new callback() {
            @Override
            public void call() {
                System.out.println(myAction.getResult().get());
            }
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            pool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
