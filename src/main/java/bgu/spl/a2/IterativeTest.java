package bgu.spl.a2;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Or on 19/12/2017.
 */
public class IterativeTest {
    public static void main(String[] args) {
        final int numOfActors = 150;
        final int actionsPerActor = 100;
        CountDownLatch latch = new CountDownLatch(numOfActors);
        ActorThreadPool pool = new ActorThreadPool(20);
        for(int i = 1; i <= numOfActors; i++){
            final int id = i;
            pool.submit(new Action<String>() {
                @Override
                protected void start() {
                    setActionName("Created actor " + id);
                    complete("Actor " + id + " created");
                }
            }, "Actor" + id, new PrivateState() {});
        }
        pool.start();
        // for each of the actors we created we will add one big action
        for(int j = 1; j <= numOfActors; j++){
            final int currentActorID = j;
            String currentActorName = "Actor" + j;
            PrivateState actor = new PrivateState() {};
            Collection<Action<String>> actions = new LinkedList<>();
            Action<String> bigAction = new Action<String>() {
                @Override
                protected void start() {
                    setActionName("BigAction of " + currentActorName);
                    //the big actions will send sub-actions to other actors
                    for(int i = 1; i <= actionsPerActor - 1; i++){
                        final String nextActor = "Actor" + (1 + ((currentActorID + i) % (numOfActors)));
                        final int name = i;
                        Action<String> subAction = new Action<String>() {
                            @Override
                            protected void start() {
                                setActionName("SubAction " + name);
                                complete("Action num " + name + " is completed");
                            }
                        };
                        actions.add(subAction);
                        sendMessage(subAction, nextActor, pool.getPrivateState(nextActor));
                    }
                    // after all the sub-actions are finished we will finish the big action
                    then(actions, new callback() {
                        @Override
                        public void call() {
                            complete("Big action of " + currentActorName + " is completed");
                        }
                    });
                }
            };
            pool.submit(bigAction, currentActorName, actor);
            bigAction.getResult().subscribe(new callback() {
                @Override
                public void call() {
                    System.out.println(bigAction.getResult().get());
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            pool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("COMPLETED");
    }
}
