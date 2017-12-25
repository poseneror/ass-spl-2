package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

public class Unregister extends Action<String> {

    private String studentName;

    public Unregister(String studnetName){
        this.studentName = studnetName;
        setActionName("Unregister");
    }

    @Override
    protected void start() {
        CoursePrivateState course = (CoursePrivateState) pool.getPrivateState(actorID);
        String courseName = actorID;
        StudentPrivateState student = (StudentPrivateState) pool.getPrivateState(studentName);

        // we need to match the number of messages sent to the number in ParticipateInCourse in order to achieve the right order of actions performed
        Action<String> waitForStud = new Action<String>() {
            @Override
            protected void start() {
                complete("ready");
            }
        };
        sendMessage(waitForStud, studentName, student);
        List<Action<String>> actions = new ArrayList<>();
        actions.add(waitForStud);
        then(actions, new callback() {
            @Override
            public void call() {
                course.removeStudent(studentName);
                Action<String> removeGrade = new Action<String>() {
                    @Override
                    protected void start() {
                        student.getGrades().remove(courseName);
                        complete("Grade removed from student - " + studentName);
                    }
                };
                sendMessage(removeGrade, studentName, student);
                List<Action<String>> actions = new ArrayList<>();
                actions.add(removeGrade);
                then(actions, new callback() {
                    @Override
                    public void call() {
                        complete(studentName + " unregistered from " + courseName);
                    }
                });
            }
        });
    }
}
