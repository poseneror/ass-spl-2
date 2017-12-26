package bgu.spl.a2;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * an abstract class that represents private states of an actor
 * it holds actions that the actor has executed so far 
 * IMPORTANT: You can not add any field to this class.
 */
public abstract class PrivateState implements Serializable{

	//TODO: is it allowed to add a constructor here?
	public PrivateState(){
		history = new LinkedList<>();
	}

	// holds the actions' name what were executed
	private List<String> history;

	public List<String> getLogger(){
		return history;
	}
	
	/**
	 * add an action to the records
	 *  
	 * @param actionName
	 */
	synchronized public void addRecord(String actionName){
		history.add(actionName);
	}
	
	
}
