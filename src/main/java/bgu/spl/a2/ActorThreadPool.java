package bgu.spl.a2;

import java.util.*;
import java.util.concurrent.*;
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
	private Map<String, ConcurrentLinkedDeque<Action>> actorsQueues;
	private Map<String, AtomicBoolean> isActorLocked;
	private Map<String, PrivateState> actorsStates;
	private List<Thread> threads;
	private final VersionMonitor actionsVM;
	private final boolean[] shutdown = {false};
	private final CountDownLatch shutDownLatch;

	/**
	 * getter for actors
	 * @return actors
	 */
	synchronized public Map<String, PrivateState> getActors(){
		return actorsStates;
	}

	/**
	 * getter for actor's private state
	 * @param actorId actor's id
	 * @return actor's private state
	 */
	synchronized public PrivateState getPrivateState(String actorId){
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
		shutDownLatch = new CountDownLatch(nthreads);
		actionsVM = new VersionMonitor();
		// if 2 actions would send message to the same queue we will have concurency problems:
		actorsQueues = new HashMap<>();
		actorsStates = new HashMap<>();
		isActorLocked = new HashMap<>();
		threads = new ArrayList<>(nthreads);
		for (int i = 0; i < nthreads; i++) {
			final ActorThreadPool myPool = this;
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					while (!Thread.currentThread().isInterrupted()) {
						int version = actionsVM.getVersion();
						AssignedAction action = getAction();
						if (action != null) {
							action.getAction().handle(myPool, action.getActorID(),
									getPrivateState(action.getActorID()));
							UnlockActor(action.getActorID());
							actionsVM.inc();
						} else {
							try {actionsVM.await(version);} catch (InterruptedException ignore) {
								Thread.currentThread().interrupt();
							}
						}
					}
					shutDownLatch.countDown();
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

	class AssignedAction{
		private Action action;
		private String actorID;

		public AssignedAction(Action action, String actorID){
			this.action = action;
			this.actorID = actorID;
		}

		public Action getAction() {
			return action;
		}

		public String getActorID() {
			return actorID;
		}
	}

	private synchronized void UnlockActor(String actorID){
		isActorLocked.get(actorID).set(false);
	}

	private synchronized AssignedAction getAction(){
		for (String actorID : actorsQueues.keySet()) {
			if (isActorLocked.get(actorID).compareAndSet(false, true)) {
				if (!actorsQueues.get(actorID).isEmpty()) {
					Action action = actorsQueues.get(actorID).remove();
					return new AssignedAction(action, actorID);
				} else {
					isActorLocked.get(actorID).set(false);
				}
			}
		}
		return null;
	}

	public synchronized void submit(Action<?> action, String actorId, PrivateState actorState) {
		if(!actorsQueues.containsKey(actorId)){
			isActorLocked.put(actorId, new AtomicBoolean(false));
			actorsStates.put(actorId, actorState);
			actorsQueues.put(actorId, new ConcurrentLinkedDeque<>());
		}
		actorsQueues.get(actorId).add(action);
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
		for(Thread t : threads){
			t.interrupt();
		}
		shutdown[0] = true;
		actionsVM.inc();
		shutDownLatch.await();
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
