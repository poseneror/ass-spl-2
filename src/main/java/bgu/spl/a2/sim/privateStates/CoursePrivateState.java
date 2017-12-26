package bgu.spl.a2.sim.privateStates;

import java.util.ArrayList;
import java.util.List;

import bgu.spl.a2.PrivateState;

/**
 * this class describe course's private state
 */
public class CoursePrivateState extends PrivateState{

	private Integer availableSpots;
	private Integer registered;
	private List<String> regStudents;
	private List<String> prequisites;
	
	/**
 	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public CoursePrivateState() {
		super();
		registered = 0;
		regStudents = new ArrayList<>();
		prequisites = new ArrayList<>();
	}

	public void setAvailableSpots(Integer availableSpots) {
		this.availableSpots = availableSpots;
	}

	public void setPrequisites(List<String> prequisites) {
		this.prequisites = prequisites;
	}

	public void addStudent(String name){
		if(availableSpots != -1 && !regStudents.contains(name)){
			regStudents.add(name);
			registered++;
			availableSpots--;
		}
	}

	public void removeStudent(String name){
		if(regStudents.contains(name)){
			regStudents.remove(name);
			if(availableSpots != -1) {
				availableSpots++;
			}
			registered--;
		}
	}

	public Integer getAvailableSpots() {
		return availableSpots;
	}

	public Integer getRegistered() {
		return registered;
	}

	public List<String> getRegStudents() {
		return regStudents;
	}

	public List<String> getPrequisites() {
		return prequisites;
	}
}
