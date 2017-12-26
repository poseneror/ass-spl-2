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
        List<Action<List<String>>> actions1 = new ArrayList<>();
        Action<List<String>> closeCourse = new Action<List<String>>() {
            @Override
            protected void start() {
                course.setAvailableSpots(-1);
                complete(course.getRegStudents());
            }
        };
        sendMessage(closeCourse, courseName, course);
        actions1.add(closeCourse);
        callback first = new callback() {
            @Override
            public void call() {
                for(String studentName : closeCourse.getResult().get()){
                    Unregister unreg = new Unregister(studentName);
                    sendMessage(unreg, courseName, course);
                }
                department.removeCourse(courseName);
                complete("Course closed");
            }
        };
        then(actions1, first);
    }
}
