package bgu.spl.a2;

import java.util.Collection;

/**
 * an abstract class that represents an action that may be executed using the
 * {@link ActorThreadPool}
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 * @param <R> the action result type
 */
public abstract class Action<R> {

    private Promise promise;
    protected ActorThreadPool pool;
    protected String actorID;
    protected PrivateState actorState;
    private callback continueCallback;

    private String actionName;

    public Action(){
        promise = new Promise();
    }

	/**
     * start handling the action - note that this method is protected, a thread
     * cannot call it directly.
     */
    protected abstract void start();
    

    /**
    *
    * start/continue handling the action
    *
    * this method should be called in order to start this action
    * or continue its execution in the case where it has been already started.
    *
    * IMPORTANT: this method is package protected, i.e., only classes inside
    * the same package can access it - you should *not* change it to
    * public/private/protected
    *
    */
   /*package*/ final void handle(ActorThreadPool pool, String actorId, PrivateState actorState) {
       this.pool = pool;
       this.actorID = actorId;
       this.actorState = actorState;

       if(continueCallback == null){
           start();
       } else {
           continueCallback.call();
       }
   }
    
    
    /**
     * add a callback to be executed once *all* the given actions results are
     * resolved
     * 
     * Implementors note: make sure that the callback is running only once when
     * all the given actions completed.
     *
     * @param actions
     * @param callback the callback to execute once all the results are resolved
     */
    protected final void then(Collection<? extends Action<?>> actions, callback callback) {
        final Action act = this;
        this.continueCallback = callback;
        final int[] counter = {0};
        final int expected = actions.size();
       	for(Action action : actions){
       	    action.getResult().subscribe(new callback() {
                @Override
                public void call() {
                    synchronized (counter) {
                        counter[0]++;
                        if (counter[0] == expected) {
                            pool.submit(act, actorID, actorState);
                        }
                    }
                }
            });
        }
    }

    /**
     * resolve the internal result - should be called by the action derivative
     * once it is done.
     *
     * @param result - the action calculated result
     */
    protected final void complete(R result) {
        //TODO: check if should add here or on submit
        actorState.addRecord(getActionName());
       	promise.resolve(result);
    }
    
    /**
     * @return action's promise (result)
     */
    public final Promise<R> getResult() {
    	return promise;
    }
    
    /**
     * send an action to an other actor
     * 
     * @param action
     * 				the action
     * @param actorId
     * 				actor's id
     * @param actorState
	 * 				actor's private state (actor's information)
	 *    
     * @return promise that will hold the result of the sent action
     */
	public Promise<?> sendMessage(Action<?> action, String actorId, PrivateState actorState){
        pool.submit(action, actorId, actorState);
        return action.getResult();
	}

    public void setActionName(String actionName){
        this.actionName = actionName;
    }

	public String getActionName(){
	    return actionName;
    }


}
