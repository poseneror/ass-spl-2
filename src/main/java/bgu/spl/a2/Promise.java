package bgu.spl.a2;

import java.util.ArrayList;
import java.util.List;

/**
 * this class represents a deferred result i.e., an object that eventually will
 * be resolved to hold a result of some operation, the class allows for getting
 * the result once it is available and registering a callback that will be
 * called once the result is available.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 *
 * @param <T>
 *            the result type, <boolean> resolved - initialized ;
 */
public class Promise<T>{
	private T value;
	private List<callback> subscribers;
	private boolean resolved;

	public Promise() {
		resolved = false;
		this.subscribers = new ArrayList<>();
	}

	/**
	 *
	 * @return the resolved value if such exists (i.e., if this object has been
	 *         {@link #resolve(Object)}ed
	 * @throws IllegalStateException
	 *             in the case where this method is called and this object is
	 *             not yet resolved
	 */
	synchronized public T get() throws IllegalStateException {
		if(isResolved()){
			return value;
		}
		throw new IllegalStateException();
	}

	/**
	 *
	 * @return true if this object has been resolved - i.e., if the method
	 *         {@link #resolve(Object)} has been called on this object
	 *         before.
	 */
	public boolean isResolved() {
		return resolved;
	}


	/**
	 * resolve this promise object - from now on, any call to the method
	 * {@link #get()} should return the given value
	 *
	 * Any callbacks that were registered to be notified when this object is
	 * resolved via the {@link #subscribe(callback)} method should
	 * be executed before this method returns
	 *
     * @throws IllegalStateException
     * 			in the case where this object is already resolved
	 * @param value
	 *            - the value to resolve this promise object with
	 */


	public void resolve(T value) throws IllegalStateException{
		if(resolved){
			throw new IllegalStateException();
		}
		this.value = value;
		// this part needs to be synchronized with the subscribe method, in order to not miss anyone!
		synchronized (this) {
			this.resolved = true;
			for (callback sub : subscribers) {
				sub.call();
			}
		}
		subscribers.clear();
	}

	/**
	 * add a callback to be called when this object is resolved. If while
	 * calling this method the object is already resolved - the callback should
	 * be called immediately
	 *
	 * Note that in any case, the given callback should never get called more
	 * than once, in addition, in order to avoid memory leaks - once the
	 * callback got called, this object should not hold its reference any
	 * longer.
	 *
	 * @param callback
	 *            the callback to be called when the promise object is resolved
	 */

	public void subscribe(callback callback) {
		boolean shouldCall = false;
		// we need to synchronize the following actions, because it is possible that a callback would be added
		// after the callback calling in the Resolve method, and thus wouldn't be called at all
		synchronized (this) {
			if (isResolved()) {
				shouldCall = true;
			} else {
				subscribers.add(callback);
			}
		}
		if(shouldCall){
			callback.call();
		}
	}
}
