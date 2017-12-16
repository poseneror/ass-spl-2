package bgu.spl.a2.sim;

import bgu.spl.a2.Promise;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * represents a warehouse that holds a finite amount of computers
 *  and their suspended mutexes.
 * 
 */
public class Warehouse {

    private static class WarehouseHolder{
	    private static Warehouse ws = new Warehouse();
    }

    private Map<String, SuspendingMutex> computers;

    private Warehouse(){
        computers = new HashMap<>();
    }

    public void addComputer(String cTyoe, long successSig, long failSig){
        computers.put(cTyoe, new SuspendingMutex(new Computer(cTyoe, successSig, failSig)));
    }

    public Promise<Computer> getComputer(String cType){
        SuspendingMutex mutex = computers.get(cType);
        if(mutex != null){
            Promise<Computer> promise = mutex.down();
            return promise;
        }
        throw new NoSuchElementException();
    }

    public void releaseComputer(String cType){
        SuspendingMutex mutex = computers.get(cType);
        if(mutex != null){
            mutex.up();
        }
        throw new NoSuchElementException();
    }

    public static Warehouse getInstance(){
	    return WarehouseHolder.ws;
    }
}
