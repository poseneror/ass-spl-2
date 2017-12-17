/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;


import bgu.spl.a2.Action;
import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.sim.json.JsonAction;
import bgu.spl.a2.sim.json.JsonComputer;
import bgu.spl.a2.sim.json.JsonInput;
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
		//TODO: replace method body with real implementation
		throw new UnsupportedOperationException("Not Implemented Yet.");
	}
	
	
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

			//Phase 1:
			for(JsonAction actionConf : input.getPhase1()){
				String actionName = actionConf.getAction();

			}
//			start();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
//	private Action getAction() {
//
//	}
}
