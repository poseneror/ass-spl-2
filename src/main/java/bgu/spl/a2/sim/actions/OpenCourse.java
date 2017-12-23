package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OpenCourse extends Action<String> {

    private int capacity;
    private List<String> prequisites;
    private String courseName;

    public OpenCourse(int capacity, List<String> pre, String courseName){
        super();
        this.capacity = capacity;
        this.prequisites = pre;
        this.courseName = courseName;

        setActionName("Open Course");
    }
    @Override
    protected void start() {
        Action<String> newCourse = new Action<String>() {
            @Override
            protected void start() {
                setActionName("Create course");
                complete("Course created");
            }
        };
        CoursePrivateState course = new CoursePrivateState();
        course.setAvailableSpots(capacity);
        course.setPrequisites(prequisites);
        sendMessage(newCourse, courseName, course).subscribe(new callback() {
            @Override
            public void call() {
                complete("Course added to department");
            }
        });

        DepartmentPrivateState department = (DepartmentPrivateState) pool.getPrivateState(actorID);
        department.addCourse(courseName);
    }
}
