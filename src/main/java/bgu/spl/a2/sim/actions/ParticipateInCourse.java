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
        List<String> prerequisites = course.getPrequisites();
        StudentPrivateState student = (StudentPrivateState) pool.getPrivateState(studentName);
        String courseName = actorID;
        Action<Boolean> checkPerquisites = new Action<Boolean>() {
            @Override
            protected void start() {
                setActionName("Check perquisites for " + courseName);
                boolean pass = true;
                for (String pre : prerequisites) {
                    if (!student.getGrades().containsKey(pre)) {
                        pass = false;
                    }
                }
                if (pass) {
                    complete(true);
                } else {
                    complete(false);
                }
            }
        };
        sendMessage(checkPerquisites, studentName, student);
        List<Action<Boolean>> actions1 = new ArrayList<>();
        actions1.add(checkPerquisites);
        then(actions1, new callback() {
            @Override
            public void call() {
                if (checkPerquisites.getResult().get() && course.getAvailableSpots() > 0) {
                    course.addStudent(studentName);
                    Action<String> addGrade = new Action<String>() {
                        @Override
                        protected void start() {
                            student.getGrades().put(courseName, grade);
                            complete(courseName + " Grades added to " + studentName);
                        }
                    };
                    sendMessage(addGrade, studentName, student);
                    List<Action<String>> actions2 = new ArrayList<>();
                    actions2.add(addGrade);
                    then(actions2, new callback() {
                        @Override
                        public void call() {
                                complete(true);
                        }
                    });
                } else {
                    complete(false);
                }
            }
        });
    }
}
