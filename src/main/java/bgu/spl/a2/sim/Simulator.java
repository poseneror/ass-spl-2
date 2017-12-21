/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


import bgu.spl.a2.*;
import bgu.spl.a2.sim.actions.*;
import bgu.spl.a2.sim.json.JsonAction;
import bgu.spl.a2.sim.json.JsonComputer;
import bgu.spl.a2.sim.json.JsonInput;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;

/**main.java.
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {

	public static ActorThreadPool actorThreadPool;

	/**
	* Begin the simulation Should not be called before attachActorThreadPool()
	*/
    public static void start(){
		actorThreadPool.start();
    }
	
	/**
	* attach an ActorThreadPool to the Simulator, this ActorThreadPool will be used to run the simulation
	* 
	* @param myActorThreadPool - the ActorThreadPool which will be used by the simulator
	*/
	public static void attachActorThreadPool(ActorThreadPool myActorThreadPool){
		actorThreadPool = myActorThreadPool;
	}
	
	/**
	* shut down the simulation
	* returns list of private states
	*/
	public static HashMap<String, PrivateState> end(){
		try {
			actorThreadPool.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return (HashMap<String, PrivateState>) actorThreadPool.getActors();
	}
	private static CountDownLatch phaseCounter;
	public static void main(String [] args){
		try {
			Gson gson = new Gson();
			JsonReader reader = new JsonReader(new FileReader(args[0]));
			JsonInput input = gson.fromJson(reader, JsonInput.class);

			//SETUP:
			attachActorThreadPool(new ActorThreadPool(input.getThreads()));
			for(JsonComputer jsonComputer : input.getComputers()) {
				Warehouse.getInstance()
						.addComputer(jsonComputer.getType(), Long.parseLong(jsonComputer.getSigSuccess()),
								Long.parseLong(jsonComputer.getSigFail()));
			}

			start();

			//Phase 1:
			phaseCounter = new CountDownLatch(input.getPhase1().size());
			if(phaseCounter.getCount() != 0) {
				for (JsonAction actionConf : input.getPhase1()) {
					submitAction(actionConf);
				}
				try {
					phaseCounter.await();
					System.out.println("FINISHED PHASE 1");
					phaseCounter = new CountDownLatch(input.getPhase2().size());
					if(phaseCounter.getCount() != 0) {
						for (JsonAction actionConf : input.getPhase2()) {
							submitAction(actionConf);
						}
						try {
							phaseCounter.await();
							System.out.println("FINISHED PHASE 2");
							phaseCounter = new CountDownLatch(input.getPhase3().size());
							if(phaseCounter.getCount() != 0) {
								for (JsonAction actionConf : input.getPhase3()) {
									submitAction(actionConf);
								}
								try {
									phaseCounter.await();
									System.out.println("FINISHED PHASE 3");
									HashMap<String, PrivateState> result = end();


									FileOutputStream fout = new FileOutputStream("output.txt");
									try {
										ObjectOutputStream oos = new ObjectOutputStream(fout);
										oos.write(gson.toJson(result).getBytes());
									} catch (IOException e) {
										e.printStackTrace();
									}
//									FileOutputStream fout = new FileOutputStream("result.ser");
//									try {
//										ObjectOutputStream oos = new ObjectOutputStream(fout);
//										oos.writeObject(result);
//									} catch (IOException e) {
//										e.printStackTrace();
//									}
								} catch (InterruptedException e3) {
									e3.printStackTrace();
								}
							} else {
								end();
							}
						} catch (InterruptedException e2) {
							e2.printStackTrace();
						}
					} else {
						end();
					}
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			} else {
				end();
			}
			end();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	private static void submitAction(JsonAction actionConf) {
		Action action;
		String actorId;
		PrivateState privateState = null;
		if(actionConf.getAction().equals("Open Course")){
			action = new OpenCourse(
					Integer.parseInt(actionConf.getSpace()),
					actionConf.getPrerequisites(),
					actionConf.getCourse()
			);
			actorId = actionConf.getDepartment();
			PrivateState actorState = actorThreadPool.getPrivateState(actorId);
			if(actorState == null){
				privateState = new DepartmentPrivateState();
			} else {
				privateState = actorState;
			}
		} else if(actionConf.getAction().equals("Add Student")){
			action = new AddStudent(actionConf.getStudent());
			actorId = actionConf.getDepartment();
			PrivateState actorState = actorThreadPool.getPrivateState(actorId);
			if(actorState == null){
				privateState = new DepartmentPrivateState();
			} else {
				privateState = actorState;
			}
		} else if(actionConf.getAction().equals("Participate In Course")){
			int grade;
			if(actionConf.getGrade().get(0).equals("-")){
				grade = -1;
			} else {
				grade = Integer.parseInt(actionConf.getGrade().get(0));
			}
			action = new ParticipateInCourse(
					grade,
					actionConf.getStudent()
			);
			actorId = actionConf.getCourse();
			PrivateState actorState = actorThreadPool.getPrivateState(actorId);
			if(actorState == null){
				privateState = new CoursePrivateState();
			} else {
				privateState = actorState;
			}
		} else if(actionConf.getAction().equals("Add Spaces")){
			action = new IncreaseSpaces(Integer.parseInt(actionConf.getNumber()));
			actorId = actionConf.getCourse();
			PrivateState actorState = actorThreadPool.getPrivateState(actorId);
			if(actorState == null){
				privateState = new CoursePrivateState();
			} else {
				privateState = actorState;
			}
		} else if(actionConf.getAction().equals("Register With Preferences")){
			List<Integer> grades = new ArrayList<>();
			for(String gradeStr : actionConf.getGrade()){
				if(gradeStr == "-"){
					grades.add(-1);
				} else {
					grades.add(Integer.parseInt(gradeStr));
				}
			}
			action = new RegisterWithPreferences(
					actionConf.getPreferences(),
					grades
			);
			actorId = actionConf.getStudent();
			PrivateState actorState = actorThreadPool.getPrivateState(actorId);
			if(actorState == null){
				privateState = new StudentPrivateState();
			} else {
				privateState = actorState;
			}
		} else if(actionConf.getAction().equals("Unregister")){
			action = new Unregister(actionConf.getStudent());
			actorId = actionConf.getCourse();
			PrivateState actorState = actorThreadPool.getPrivateState(actorId);
			if(actorState == null){
				privateState = new CoursePrivateState();
			} else {
				privateState = actorState;
			}
		} else if(actionConf.getAction().equals("Close Course")){
			action = new CloseCourse(actionConf.getCourse());
			actorId = actionConf.getDepartment();
			PrivateState actorState = actorThreadPool.getPrivateState(actorId);
			if(actorState == null){
				privateState = new DepartmentPrivateState();
			} else {
				privateState = actorState;
			}
		} else if(actionConf.getAction().equals("Administrative Check")){
			action = new CheckObligations(
					actionConf.getStudents(),
					actionConf.getConditions(),
					actionConf.getComputer()
			);
			actorId = actionConf.getDepartment();
			PrivateState actorState = actorThreadPool.getPrivateState(actorId);
			if(actorState == null){
				privateState = new DepartmentPrivateState();
			} else {
				privateState = actorState;
			}
		} else {
			throw new IllegalArgumentException("Action");
		}
		action.getResult().subscribe(new callback() {
			@Override
			public void call() {
				phaseCounter.countDown();
			}
		});
		actorThreadPool.submit(action, actorId, privateState);
	}
}
