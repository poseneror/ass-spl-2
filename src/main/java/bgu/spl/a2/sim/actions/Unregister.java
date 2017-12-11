package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class Unregister extends Action {

    private String name;

    public Unregister(String name){
        this.name = name;
    }

    @Override
    protected void start() {
        CoursePrivateState course = (CoursePrivateState) pool.getPrivateState(actorID);
        course.removeStudent(name);
        StudentPrivateState studnet = (StudentPrivateState) pool.getPrivateState(name);
        studnet.getGrades().remove(actorID);
    }
}
