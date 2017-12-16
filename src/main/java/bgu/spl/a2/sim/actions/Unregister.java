package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class Unregister extends Action<String> {

    private String studentName;

    public Unregister(String studnetName){
        this.studentName = studnetName;
    }

    @Override
    protected void start() {
        CoursePrivateState course = (CoursePrivateState) pool.getPrivateState(actorID);
        course.removeStudent(studentName);
        StudentPrivateState studnet = (StudentPrivateState) pool.getPrivateState(studentName);
        studnet.getGrades().remove(actorID);
        complete("Student Unregistered");
    }
}
