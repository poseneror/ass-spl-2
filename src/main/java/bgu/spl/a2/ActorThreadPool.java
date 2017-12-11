package bgu.spl.a2;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * represents an actor thread pool - to understand what this class does please
 * refer to your assignment.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class ActorThreadPool {
	private Map<String, Queue<Action>> actorsQueues;
	private Map<String, AtomicBoolean> isActorLocked;
	private Map<String, PrivateState> actorsStates;
	private List<Thread> threads;
	private final VersionMonitor actionsVM, shutdownVM;
	private final boolean[] shutdown = {false};
	private final int[] activeCounter = {0};

	/**
	 * getter for actors
	 * @return actors
	 */
	public Map<String, PrivateState> getActors(){
		return actorsStates;
	}

	/**
	 * getter for actor's private state
	 * @param actorId actor's id
	 * @return actor's private state
	 */
	public PrivateState getPrivateState(String actorId){
		return actorsStates.get(actorId);
	}

	/**
	 * creates a {@link ActorThreadPool} which has nthreads. Note, threads
	 * should not get started until calling to the {@link #start()} method.
	 *
	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 *
	 * @param nthreads
	 *            the number of threads that should be started by this thread
	 *            pool
	 */
	public ActorThreadPool(int nthreads) {
		activeCounter[0] = 0;
		actionsVM = new VersionMonitor();
		shutdownVM = new VersionMonitor();
		// if 2 actions would send message to the same queue we will have concurency problems:
		actorsQueues = new ConcurrentHashMap<>();
		actorsStates = new HashMap<>();
		isActorLocked = new HashMap<>();
		threads = new ArrayList<>(nthreads);
		for (int i = 0; i < nthreads; i++) {
			final ActorThreadPool myPool = this;
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					//TODO: check if thread safe - for now use optimistic try and fail
					synchronized (activeCounter) {
						activeCounter[0]++;
					}
					lookForActions();
					synchronized (activeCounter){
						activeCounter[0]--;
					}
				}

				public void lookForActions() {
					int version = actionsVM.getVersion();
					boolean hasActions = false;
					try {
						for (String id : actorsQueues.keySet()) {
							if (version != actionsVM.getVersion() || shutdown[0]) {
								throw new ConcurrentModificationException();
							} else {
								if(!actorsQueues.get(id).isEmpty()){
									if(isActorLocked.get(id).compareAndSet(false, true)){
										Action action = actorsQueues.get(id).remove();
										actionsVM.inc();
										hasActions = true;
										action.handle(myPool, id, actorsStates.get(id));
										isActorLocked.get(id).set(false);
									}
								}
							}
						}
						if(!hasActions && version == actionsVM.getVersion()){
							try{
								actionsVM.await(version);
							} catch (InterruptedException ex){

							}
						}
						throw new ConcurrentModificationException();
					} catch (ConcurrentModificationException ex){
						if(!shutdown[0]) {
							lookForActions();
						} else {
							// let the shutdown know one more thread is down
							shutdownVM.inc();
						}
					}
				}
			});
			threads.add(t);
		}
	}

	/**
	 * submits an action into an actor to be executed by a thread belongs to
	 * this thread pool
	 *
	 * @param action
	 *            the action to execute
	 * @param actorId
	 *            corresponding actor's id
	 * @param actorState
	 *            actor's private state (actor's information)
	 */
	public void submit(Action<?> action, String actorId, PrivateState actorState) {
		//TODO: chack toString();
		// we use concurrent hash maps to avoid multiple value putting in the map
		if(!actorsQueues.containsKey(actorId)){
			//TODO: find a normal queue
			actorsQueues.put(actorId, new ConcurrentLinkedQueue<>());
			isActorLocked.put(actorId, new AtomicBoolean(false));
			actorsStates.put(actorId, actorState);
		}
		actorsQueues.get(actorId).add(action);
		//TODO: where do we store them?
		actionsVM.inc();
	}

	/**
	 * closes the thread pool - this method interrupts all the threads and waits
	 * for them to stop - it is returns *only* when there are no live threads in
	 * the queue.
	 *
	 * after calling this method - one should not use the queue anymore.
	 *
	 * @throws InterruptedException
	 *             if the thread that shut down the threads is interrupted
	 */
	public void shutdown() throws InterruptedException {
		shutdown[0] = true;
		actionsVM.inc(); // to put out of await()
		while(activeCounter[0] != 0){
			try {
				shutdownVM.await(shutdownVM.getVersion());
			} catch (InterruptedException ex){

			}
		}
	}

	/**
	 * start the threads belongs to this thread pool
	 */
	public void start() {
		for(Thread t : threads){
			t.start();
		}
	}

}
