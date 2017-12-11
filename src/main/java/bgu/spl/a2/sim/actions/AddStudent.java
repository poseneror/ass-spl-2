package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

public class AddStudent extends Action {

    private String name;

    public AddStudent(String name){
        super();
        this.name = name;
    }

    @Override
    protected void start() {
        DepartmentPrivateState department = (DepartmentPrivateState) pool.getPrivateState(actorID);
        department.addStudent(name);
    }

}
