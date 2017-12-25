package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class CloseCourse extends Action<String> {

    private String courseName;

    public CloseCourse(String courseName){
        this.courseName = courseName;
        setActionName("Close Course");
    }

    @Override
    protected void start() {
        final DepartmentPrivateState department = (DepartmentPrivateState) pool.getPrivateState(actorID);
        final CoursePrivateState course = (CoursePrivateState) pool.getPrivateState(courseName);

        // we need to match the order if people participate in the same time we close course! - first step, submit to course
        List<Action<List<String>>> actionList = new ArrayList<>();
        Action<List<String>> getRegStudents = new Action<List<String>>() {
            @Override
            protected void start() {
                course.setAvailableSpots(-1);
                complete(course.getRegStudents());
            }
        };
        sendMessage(getRegStudents, courseName, course);
        actionList.add(getRegStudents);
        then(actionList, new callback() {
            @Override
            public void call() {
                List<Action<String>> actionList = new ArrayList<>();
                for(String studentName : getRegStudents.getResult().get()){
                    Unregister unreg = new Unregister(studentName);
                    actionList.add(unreg);
                    sendMessage(unreg, courseName, course);
                }
                then(actionList, new callback() {
                    @Override
                    public void call() {
                         department.removeCourse(courseName);
                        complete("Course closed");
                    }
                });
            }
        });
    }
}
