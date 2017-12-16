package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.ArrayList;
import java.util.Collection;

public class CloseCourse extends Action<String> {

    private String courseName;

    public CloseCourse(String courseName){
        this.courseName = courseName;
    }

    @Override
    protected void start() {
        final DepartmentPrivateState department = (DepartmentPrivateState) pool.getPrivateState(actorID);
        final CoursePrivateState course = (CoursePrivateState) pool.getPrivateState(courseName);
        Collection<Action<String>> actionList = new ArrayList<>();
        for(String studentName : course.getRegStudents()){
            Unregister unreg = new Unregister(studentName);
            actionList.add(unreg);
            sendMessage(unreg, courseName, course);
        }
        then(actionList, new callback() {
            @Override
            public void call() {
                department.removeCourse(courseName);
                course.setAvailableSpots(-1);
            }
        });

    }
}
