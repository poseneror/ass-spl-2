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
        // When we know if the student can register
        sendMessage(checkPerquisites, studentName, student).subscribe(new callback() {
            @Override
            public void call() {
                if (checkPerquisites.getResult().get()) {
                    Action<Boolean> addIfThereIsPlace = new Action<Boolean>() {
                        @Override
                        protected void start() {
                            if (course.getAvailableSpots() > 0) {
                                course.addStudent(studentName);
                                complete(true);
                            } else {
                                complete(false);
                            }
                        }
                    };
                    sendMessage(addIfThereIsPlace, courseName, course).subscribe(new callback() {
                        @Override
                        public void call() {
                            if(addIfThereIsPlace.getResult().get()) {
                                Action<String> putGrade = new Action<String>() {
                                    @Override
                                    protected void start() {
                                        setActionName("Register to course");
                                        student.getGrades().put(courseName, grade);
                                        complete("Updated Course Grade");
                                    }
                                };
                                sendMessage(putGrade, studentName, student).subscribe(new callback() {
                                    @Override
                                    public void call() {
                                        complete(true);
                                    }
                                });
                            } else {
                                // no available space
                                complete(false);
                            }
                        }
                    });
                } else {
                    // no prerequisites
                    complete(false);
                }
            }
        });
    }
}
