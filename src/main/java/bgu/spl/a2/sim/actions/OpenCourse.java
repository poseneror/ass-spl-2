package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.List;

public class OpenCourse extends Action {

    private int capacity;
    private List<String> prequisites;
    private String name;

    public OpenCourse(int capacity, List<String> pre, String name){
        super();
        this.capacity = capacity;
        this.prequisites = pre;
        this.name = name;
    }
    @Override
    protected void start() {
        CoursePrivateState course = new CoursePrivateState();
        course.setAvailableSpots(capacity);
        course.setPrequisites(prequisites);
        DepartmentPrivateState department = (DepartmentPrivateState) pool.getPrivateState(actorID);
        //TODO: if i was submited to a student?
        department.addCourse(name);
    }

}
