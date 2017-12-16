package bgu.spl.a2;

import bgu.spl.a2.sim.Warehouse;

import java.util.Collection;
import java.util.LinkedList;

public class main {
    public static void main(String[] args) {
        ActorThreadPool pool = new ActorThreadPool(2);
        pool.start();
        Action<Integer> bigAction = new Action<Integer>() {
            @Override
            protected void start() {
                setActionName("ACTION2");
                Collection<Action<Integer>> actions = new LinkedList<>();
                Action<Integer> myAction = new Action<Integer>() {
                    @Override
                    protected void start() {
                        setActionName("ACTION1");
                        Integer ans = 10;
                        complete(ans);
                    }
                };
                Action<Integer> myAction2 = new Action<Integer>() {
                    @Override
                    protected void start() {
                        setActionName("ACTION12");
                        Integer ans = 4;
                        complete(ans);
                    }
                };
                actions.add(myAction);
                actions.add(myAction2);
                then(actions, new callback() {
                    @Override
                    public void call() {
                        Integer sum = 0;
                        for(Action<Integer> act : actions){
                            sum += act.getResult().get();
                            sum += 1;
                        }
                        complete(sum);
                    }
                });
                sendMessage(myAction, "daniel", new PrivateState(){});
                sendMessage(myAction2, "danidel", new PrivateState(){});
            }
        };

        pool.submit(bigAction, "or", new PrivateState(){});
        bigAction.getResult().subscribe(new callback() {
            @Override
            public void call() {
                System.out.println(bigAction.getResult().get());
            }
        });
//        try {
//            Thread.sleep(1000);
//            pool.shutdown();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
