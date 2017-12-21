package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

public class ParticipateInCourse extends Action<Boolean> {

    private Integer grade;
    private String studentName;
    public ParticipateInCourse(Integer grade, String studentName){
        super();
        this.grade = grade;
        this.studentName = studentName;

        setActionName("Participate In Course");
    }

    @Override
    protected void start() {
        CoursePrivateState course = (CoursePrivateState) pool.getPrivateState(actorID);
        List<String> prequisites = course.getPrequisites();
        StudentPrivateState student = (StudentPrivateState) pool.getPrivateState(studentName);
        String courseName = actorID;
        //TODO: check if can implement atomic integers - what if 2 students register at the same time when there is one spot left
        if(course.getAvailableSpots() > 0){
            Action<Boolean> checkPerquisites = new Action<Boolean>() {
                @Override
                protected void start() {
                    setActionName("Check perquisites for " + courseName);
                    boolean pass = true;
                    for(String pre : prequisites){
                        if(!student.getGrades().containsKey(pre)){
                            pass = false;
                        }
                    }
                    if(pass){
                        complete(true);
                    } else {
                        complete(false);
                    }
                }
            };
            sendMessage(checkPerquisites, studentName, student);
            List<Action<Boolean>> actions = new ArrayList<>();
            actions.add(checkPerquisites);
            Action callingAction = this;
            then(actions, new callback() {
                @Override
                public void call() {
                    if(checkPerquisites.getResult().get()){
                        Action<String> putGrade = new Action<String>() {
                            @Override
                            protected void start() {
                                setActionName("Register to course");
                                student.getGrades().put(actorID, grade);
                                complete("Updated Course Grade");
                            }
                        };
                        sendMessage(putGrade, studentName, student);
                        List<Action<String>> actions = new ArrayList<>();
                        actions.add(putGrade);
                        then(actions, new callback() {
                            @Override
                            public void call() {
                                course.addStudent(studentName);
                                complete(true);
                            }
                        });
                    } else {
                        complete(false);
                    }
                }
            });
        } else {
            complete(false);
        }
    }
}
